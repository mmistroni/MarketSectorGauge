from flask import Flask
from flask import request
from flask import jsonify
from flask import render_template
from werkzeug import secure_filename
from sectorFinder import sectorFinder, strategyWorker, yahooClient, sqliteConnector
from sender import rabbitmq_sender
import os
import sys
import signal
import atexit

dbConnector = sqliteConnector()
yahoo_client = yahooClient()
sFinder = sectorFinder(yahoo_client, dbConnector)
strategy = strategyWorker(yahoo_client, dbConnector)
mqsender = rabbitmq_sender()


UPLOAD_FOLDER = '/var/www/worldcorp/downloads/'


app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER


@app.route('/upload', methods=['GET', 'POST'])
def upload_file():
    if request.method == 'POST':
        file = request.files['file']
        if file :
	    try:
		    print 'setting new filename:',file.filename
		    filename = secure_filename(file.filename)
		    print 'about to ave'
		    file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
		    return '<html><body>Success</body></html>'
	    except Exception as e:
		    return '<html><body' + str(e) + '</body></html>'
    return '''
    <!doctype html>
    <title>Upload new File</title>
    <h1>Upload new File</h1>
    <form action="" method=post enctype=multipart/form-data>
      <p><input type=file name=file>
         <input type=submit value=Upload>
    </form>
    '''


@app.route('/rabbitmq', methods=['GET', 'POST'])
def send_message():
    if request.method == 'POST':
	print request.args.get('message')
	msg =  request.form['message']
	try:
	    mqsender.sendMessage(msg)
	    return '<html><body>Sent:' + msg + '</body></html>'	
	except Exception as e:
		return '<html><body>' + str(e) + '</body></html>'
    return '''
    <!doctype html>
    <title>Send a RabbitMq message</title>
    <h1>Enter Message</h1>
    <form action="" method=post enctype=multipart/form-data>
      <p><input type=textarea name=message>
         <input type=submit value=Upload>
    </form>
    '''




@app.route('/sectors',methods=['GET', 'POST'])
def viewAllSectors():
	sectorData = dbConnector.executeSql('select name, sectorId, ticker from sectors')
	data_to_return = []	
	for sector in sectorData:
		data_to_return.append({'sectorName':sector[0], 'sectorId':sector[1], 'ticker':sector[2]})
	
	return jsonify({'root': data_to_return})		


@app.route('/sectors/performance',methods=['GET', 'POST'])
def viewTopPerformingSectors():
	# input params come as request params
	print 'loading top performing sectors'

	json = 	request.json

	if json is not None:
		dictionary = dict(json)
		bestSector = strategy.fetchBestSectors(dictionary)
		return jsonify({'root': bestSector})		
	else :
		return 'Invalid REquest'

@app.route('/sector/perf',methods=['GET', 'POST'])
def viewSectorPerformance():
	# input params come as request params
	print 'loading performance for sectors'

	json = 	request.json

	if json is not None:
		dictionary = dict(json)
		print dictionary
		if 'ticker' not in dictionary:
			return invalidRequest()
		bestSector = strategy.fetchSectorPerformance(dictionary)
		
		return jsonify({'root': bestSector})		
	else :
		return invalidRequest()

@app.route('/sector/companies',methods=['GET', 'POST'])
def viewSectorCompanies():
	# input params come as request params
	print 'loading companies for sectors'

	json = 	request.json

	if json is not None:
		dictionary = dict(json)
		if 'ticker' not in dictionary:
			return invalidRequest()
		sectorCompanies = strategy.fetchSectorCompanies(dictionary)
		
		return jsonify({'root' :sectorCompanies})		
	else :
		return invalidRequest()



@app.route('/sector/worstperf',methods=['GET', 'POST'])
def viewSectorWorstPerformance():
	# input params come as request params
	print 'loading performance for sectors'

	json = 	request.json

	if json is not None:
		dictionary = dict(json)
		worstSector = strategy.fetchWorstSectorsPerformance(dictionary)
		return jsonify({'root': worstSector})		
	else :
		return invalidRequest()



@app.route('/companies/view/<ticker>',methods=['GET', 'POST'])
def viewCompany(ticker):
	# To replace to avoid sql injection
	
	'''
	companyData = dbConnector.execute('select name, ticker,priceToEarnings real, Roe real, DivYield real, LtDToEq real, PriceToBook real, NetProfMgn real, PriceToCashFlow,lastupdate from companies where ticker=\'%s\'' % (ticker))
	
	'''
	companyData = dbConnector.executeSql('select name, ticker,priceToEarnings real, Roe real, DivYield real, LtDToEq real, PriceToBook real, NetProfMgn real, PriceToCashFlow,lastupdate from companies where ticker=?' ,[ticker])
	
	data_to_return = []	
	if len(companyData) > 0:	
		for row in companyData:
			data_to_return.append({'name':row[0], 'ticker':row[1], 'priceToEarnings':row[2], 'ROE':row[3], 'Dividend Yield':row[4], 'Long Term Debt To Equity' :row[5], 'Price To Book': row[6], 'Net Profit Margin':row[7], 'Price To CashFlow': row[8], 'LastUpdateTime':row[9]})
	else:
		data_to_return =  yahoo_client.get_quote(ticker)		
	return jsonify({'root': data_to_return})


@app.route('/companies/lookup',methods=['GET', 'POST'])
def companyLookup():
	# To replace to avoid sql injection
	json = request.json
	if json is not None:
		dictionary = dict(json)
		ticker = dictionary['ticker']	
		
		companyData = dbConnector.executeSql('select name, ticker,priceToEarnings real, Roe real, DivYield real, LtDToEq real, PriceToBook real, NetProfMgn real, PriceToCashFlow,lastupdate from companies where ticker=?' ,[ticker])
		data_to_return = []	
		if len(companyData) > 0:	
			for row in companyData:
				data_to_return.append({'name':row[0], 'ticker':row[1], 'priceToEarnings':row[2], 'ROE':row[3], 'Dividend Yield':row[4], 'Long Term Debt To Equity' :row[5], 'Price To Book': row[6], 'Net Profit Margin':row[7], 'Price To CashFlow': row[8], 'LastUpdateTime':row[9]})
		else:
			data_to_return =  {ticker : 'No data found'}		
		return jsonify({'root': data_to_return})
	return invalidRequest




@app.route('/versioninfo',methods=['GET', 'POST'])
def versioninfo():
	last_update = dbConnector.executeSql('select max(asofdate) from stock_prices')[0][0]
	version = 1.1
	
	return jsonify({'root':{'version': version, 'last_update' : last_update, 'copyright' : '@2012 WorldCorpServices Ltd'}})


@app.route('/app/metadata',methods=['GET', 'POST'])
def metadata():
	
	return jsonify({'root':{'/versioninfo': ['Display Version Info','params:None'], '/companies/lookup' : ['Lookup Companies Data','params:ticker'],'/companies/view/<ticker>': ['Returns Latest Stock Price', 'Params:None'], '/sectors' :['Display All Sectors','Params:None'],  '/sector/performance' :['Display Best Performing Sectors','Params:months{3|6|3/6}'],'/sector/worstperf' :['Display Worst Performing Sectors','Params:months{3|6|3/6}'], '/sector/perf' :['Display Performance for a Sectors','Params:months{3|6|3/6}']}})




@app.route('/about')
def about():
	return "@Copyright 2012 WorldCorp Services Ltd"


@app.route('/shares/upload',methods=['GET', 'POST'])
def upload_shares():
	json = request.json
	if json is not None:
		try:
			dictionary = dict(json)
			strategy.uploadShare(dictionary)
			return jsonify({'root':{'status':'success'}})		
		except Exception as e:
			print e
			return jsonify({'root':{'status':'failed to upload share'}})
	return invalidRequest()


@app.route('/shares',methods=['GET', 'POST'])
def fetch_shares():
	json = request.json
	if json is not None:
		
		dictionary = dict(json)
		ticker = dictionary['ticker']	
		#share_data = dbConnector.executeSql('select ticker, asofdate, price from stock_prices where ticker=? order by asofdate desc' ,[ticker])
		#if len(share_data) > 0:
		#	return jsonify({'root':{'ticker':share_data[0][0], 'asofdate':share_data[0][1],'price':share_data[0][2]}})	
		return jsonify(yahoo_client.fetchDataFromYahoo(dictionary['ticker']))
	return invalidRequest()


@app.route('/post/<int:post_id>')
def show_post(post_id):
	return 'Post %d ' % post_id

@app.errorhandler(404)
def not_found(error):
    response = jsonify({'code': 404,'message': 'No interface defined for URL:' + request.path})
    return response

# Jinja requests
@app.route("/template_test")
def template_test():
    rand_list = [0,1,2,3,4,5]
    return render_template('layout.html', a_random_string="Heey, what's up!", a_random_list=rand_list)


import signal

def invalidRequest():
	return jsonify({'root':{'Error':'Invalid Request'}})




def handler():
    print 'Shutting down...'
    f = open('log.txt', 'w')
    f.write('shutdown...')
    f.close()
    sys.exit()

#signal.signal(signal.SIGTERM, handler)
#signal.pause()

if __name__== "__main__":
        app.run(host='0.0.0.0')
