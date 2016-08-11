from __future__ import division

import os
import re
import sys
import urllib2
import csv
import simplejson
import json
import StringIO
from datetime import datetime, timedelta, date
from apscheduler.scheduler import Scheduler
from dateutil.relativedelta import relativedelta
import logging
import sqlite3
import csv
import atexit
import logging
import logging.handlers
import zipfile
import codecs
import sys
import os
import time
import glob


mydb_path = 'shares.db'
exportPath = 'shares.csv'
exportSectorPath = 'sectors.csv'
exportCompaniesPath = 'companies.csv'
exportSharesPath='shares_names.csv'



# all sectors url http://biz.yahoo.com/ic/ind_index_alpha.html

# sector specific url http://biz.yahoo.com/ic/<sectorId>.html

# list of companies http://biz.yahoo.com/p/csv/<sectorid>conameu.csv


class TimedCompressedRotatingFileHandler(logging.handlers.TimedRotatingFileHandler):
    """
    Extended version of TimedRotatingFileHandler that compress logs on rollover.
    """
    def doRollover(self):
        """
        do a rollover; in this case, a date/time stamp is appended to the filename
        when the rollover happens.  However, you want the file to be named for the
        start of the interval, not the current time.  If there is a backup count,
        then we have to get a list of matching filenames, sort them and remove
        the one with the oldest suffix.
        """

        self.stream.close()
        # get the time that this sequence started at and make it a TimeTuple
        t = self.rolloverAt - self.interval
        timeTuple = time.localtime(t)
        dfn = self.baseFilename + "." + time.strftime(self.suffix, timeTuple)
        if os.path.exists(dfn):
            os.remove(dfn)
        os.rename(self.baseFilename, dfn)
        if self.backupCount > 0:
            # find the oldest log file and delete it
            s = glob.glob(self.baseFilename + ".20*")
            if len(s) > self.backupCount:
                s.sort()
                os.remove(s[0])
    	#print "%s -> %s" % (self.baseFilename, dfn)
    	if self.encoding:
		self.stream = codecs.open(self.baseFilename, 'w', self.encoding)
    	else:
		self.stream = open(self.baseFilename, 'w')
    	self.rolloverAt = self.rolloverAt + self.interval
    	if os.path.exists(dfn + ".zip"):
		os.remove(dfn + ".zip")
    	file = zipfile.ZipFile(dfn + ".zip", "w")
    	file.write(dfn, os.path.basename(dfn), zipfile.ZIP_DEFLATED)
    	file.close()
    	os.remove(dfn)





logging.basicConfig(level=logging.DEBUG,filename='./debug.log',filemode="w")
loghandler = TimedCompressedRotatingFileHandler("./debug.log",when="midnight")
logger = logging.getLogger()
logger.setLevel(logging.INFO)
logger.addHandler(loghandler)
		

class csvWorker(object):

	def exportData(self, dataList, filename):
		f = open(filename, 'w')
		dbWriter = csv.writer(f)
		for row in dataList:
			dbWriter.writerow(row)
		f.close()
	def importData(self, csvFile):
		dbRows = []	
		with open(csvFile, 'rb') as f:
    			reader = csv.reader(f)
    			for row in reader:
        			dbRows.append(row)
		return dbRows

	def openUrl(self, url) :
		csvdata = []
		f = urllib2.urlopen(url)
		datareader  = csv.reader((line.replace('\0','') for line in f))

		for item in datareader:
			csvdata.append(item)
		return csvdata[1:]


		
class sqliteConnector(object):
	def __init__(self):
		atexit.register(self.onShutdown)
		self.csvWorker = csvWorker()
		self.con = sqlite3.connect(':memory:',check_same_thread=False)
		#self.conrow_factory=sqlite3.Row
		self.con.text_factory = str
		self.cursor = self.con.cursor()
		self.con.execute('''create table stock_prices
      			( ticker varchar(8) not null, asofdate datetime not null, price real, currentEps real, forwardEps real, movingAvg real,shortratio real, exDivDate varchar(10) ,primary key (ticker, asofdate) )''')
		self.con.execute('create table sectors(name varchar(40) not null primary key, ticker varchar(15), sectorId varchar(5), source varchar[4])')    						
		self.con.execute('''create table companies(name varchar(50) not null primary key,priceChange real,marketCap real, priceToEarnings real, Roe real, DivYield real, LtDToEq real, PriceToBook real, NetProfMgn real, PriceToCashFlow real, sectorId varchar(5), ticker varchar(20), lastupdate)''')
		self.con.execute('create table shares(ticker varchar(15) not null primary key, name varchar(40), source varchar[4])')    						
			

		if os.path.exists(exportPath):
    			#create new DB, create table stocks
			print 'Importing existing shares data......'
			self.importShares()
		if os.path.exists(exportSectorPath):
    			#create new DB, create table stocks
			print 'Importing existing sector data......'
			self.importSectors()
		if os.path.exists(exportCompaniesPath):
    			#create new DB, create table stocks
			print 'Importing existing companies data......'
			self.importCompanies()
		if os.path.exists(exportSharesPath):
    			#create new DB, create table stocks
			print 'Importing existing share names data......'
			#self.importCompanies()
	
	def storeSector(self, sector):
		#print 'inserting sector', sector
		sectorSql = 'INSERT or REPLACE INTO sectors(name, ticker, sectorId,source) VALUES (?,?,?,?)'
		self.cursor.execute(sectorSql, (sector['name'],sector['ticker'], sector['sectorId'], sector['source']))
		self.con.commit()

	def store(self, shareData):
		#print 'inserting:',shareData
		storeSql = 'INSERT OR REPLACE INTO stock_prices(ticker, asofdate,price,currentEps,forwardEps,movingAvg,shortratio,exDivDate) VALUES(?,?,?,?,?,?,?,?)'
		self.cursor.execute(storeSql,  (shareData['ticker'], datetime.strptime(shareData['asofdate'], '%Y-%m-%d %H:%M:%S'), shareData['latest'],shareData['currentEps'],shareData['forwardEps'],shareData['movingAvg'], shareData['shortratio'],shareData['exDiv']))
		self.con.commit()
	
	def storeCompanies(self, company):
		#print 'storing company:', company['Description']
		storeSql = 'INSERT OR REPLACE INTO companies(name ,priceChange,marketCap,priceToEarnings,Roe,DivYield,LtDToEq,PriceToBook,NetProfMgn, PriceToCashFlow,sectorId, ticker, lastupdate) VALUES(?,?,?,?,?,?,?,?,?,?,?,?, ?)'
		self.con.execute(storeSql, (company['Description'],company['PriceChange'],company['MarketCap'],company['PriceToEarnings'], company['Roe'], company['DivYield'], company['LtDToEq'],company['PriceToBook'],company['NetProfMgn'],company['PriceToCashFlow'],company['sectorId'],company['ticker'],
datetime.now()))
		self.con.commit()

	def onShutdown(self):
		print 'shutting down and exporting database...'
		print 'exporting shares...'		
		self.cursor.execute('SELECT ticker,asofdate,price,currentEps,forwardEps,movingAvg,shortratio,exDivDate from stock_prices order by asofdate asc')
		stockRows = self.cursor.fetchall()
		self.csvWorker.exportData(stockRows,exportPath)
		print 'exporting sectors.....'
		self.cursor.execute('select * from sectors')
		sectorRows = self.cursor.fetchall()		
		self.csvWorker.exportData(sectorRows, exportSectorPath)
		print 'exporting companies...'
		self.cursor.execute('select * from companies')
		companiesRows = self.cursor.fetchall()
		self.csvWorker.exportData(companiesRows,exportCompaniesPath)	
	
	def exportPrices(self):
		print 'exporting shares...'		
		self.cursor.execute('SELECT ticker,asofdate,price,currentEps,forwardEps,movingAvg,shortratio,exDivDate from stock_prices order by asofdate asc')
		stockRows = self.cursor.fetchall()

		dateStr = datetime.now().strftime('%Y%m%d')

		logger.info('Exporting "%s"."%s"' , exportPath, dateStr)

		self.csvWorker.exportData(stockRows,exportPath)
		


        def importShares(self):
		rowData = self.csvWorker.importData(exportPath)
		print 'importing %s shares....' % (len(rowData))
				
		for row in rowData:
			stockMap = {'ticker':row[0],'asofdate':row[1],'latest':row[2],'currentEps':row[3],'forwardEps':row[4],'movingAvg':row[5],'shortratio':row[6],'exDiv':row[7]}
			self.store(stockMap)
	def importSectors(self):
		
		sectorData = self.csvWorker.importData(exportSectorPath)
		print 'importing %s sectors' % (len(sectorData))		
		for row in sectorData:
			sectorMap = {'name':row[0],'ticker':row[1], 'sectorId':row[2],'source': 'yhoo' if len(row) < 4 else row[3]}
			self.storeSector(sectorMap)
	def importCompanies(self):
		companiesData = self.csvWorker.importData(exportCompaniesPath)
		print 'importing %s companies' % (len(companiesData))
				
		for row in companiesData:
			company = {'Description':row[0],'PriceChange':row[1], 'MarketCap':row[2], 'PriceToEarnings':row[3], 'Roe':row[4], 'DivYield':row[5], 'LtDToEq':row[6], 'PriceToBook':row[7],'NetProfMgn':row[8], 'PriceToCashFlow':row[9], 'sectorId':row[10], 'ticker':row[11]}
			self.storeCompanies(company)	

	def getAllData(self):
		self.cursor.execute('SELECT * FROM stock_prices')
		return self.cursor.fetchall()

	def executeSql(self, sql, params=[]):
		self.cursor.execute(sql,params)
		return self.cursor.fetchall()	

	def execute(self, sql, params=[]):
		self.cursor.execute(sql,params)
		return self.cursor.fetchall()	

################# SQLITE CONNECTOR ################################





######### YAHOO IMPORTER #############
# view all industries for a sector. the one below is Financials
#http://biz.yahoo.com/p/csv/431conameu.csv

# list of all sectors from yahoo
#http://biz.yahoo.com/ic/ind_index_alpha.html

# historical prices
#'http://ichart.finance.yahoo.com/table.csv?s=%(ticker)s&a=%(fromMonth)s&b=%(fromDay)s&c=%(fromYear)s&d=%(toMonth)s&e=%(toDay)s&f=%(toYear)s&g=d&ignore=.csv' 



class yahooClient(object):
	
	def __init__(self):
		self.yahooUrl = 'http://finance.yahoo.com/d/quotes.csv?s=%s&f=sl1d1e7e8m4qr5s7'
		self.lookupUrl = "http://d.yimg.com/autoc.finance.yahoo.com/autoc?query=%s&callback=YAHOO.Finance.SymbolSuggest.ssCallback"
		self.historicalPricesUrl='http://ichart.finance.yahoo.com/table.csv?s=%(ticker)s&a=%(fromMonth)s&b=%(fromDay)s&c=%(fromYear)s&d=%(toMonth)s&e=%(toDay)s&f=%(toYear)s&g=d&ignore=.csv'
		self.csv_worker = csvWorker()
		
	def fetchDataFromYahoo(self, ticker):
		if ticker.startswith('DJ') or ticker.startswith('DW') or ticker.startswith('.'):
			logger.info('fetching from google..')
			return self.get_quote(ticker)
		logger.info(self.yahooUrl % (ticker))
		try:
			f = urllib2.urlopen(self.yahooUrl % (ticker))
			data = f.read()
			logger.info(data)
			stockdata = [item.strip('"').strip('\r\n') for item in data.split(',')]
						
			return {'ticker':stockdata[0], 'latest':stockdata[1],'asofdate':stockdata[2],'currentEps':stockdata[3],'forwardEps':stockdata[4],'movingAvg':stockdata[5], 'exDiv':stockdata[6], 'peg':stockdata[7],'shortratio':stockdata[8]}
		except:
			logger.error('failed to fetch data for:%s' % (ticker))
			return {}
		

	def get_quote(self, ticker_symbol):
		# Getting quote via Yahoo	
    		url = 'http://finance.google.com/finance/info?q=%s' % ticker_symbol

		logger.info('google opening url:%s' % url)

		try:
    			lines = urllib2.urlopen(url).read().splitlines()
			quote = json.loads(''.join([x for x in lines if x not in ('// [', ']')]))
			logger.info( 'ticker: %s' % quote['t']) 
    			logger.info('current price: %s' % quote['l_cur'])
   			#logger.info('last trade: %s' % quote['lt'])
			return {'ticker': quote['t'], 'latest': float(quote['l_cur'].replace(',','')), 'asofdate': datetime.today().strftime('%m/%d/%Y'),'currentEps': 0.0,'forwardEps': 0.0,'movingAvg':0.0, 'exDiv':'N/A', 'peg':0.0,'shortratio':0.0}
		except:
			logger.error('failed to fetch data for:%s' % (ticker_symbol))
			return {}
		
   	def get_from_bloomberg(self, ticker):
		bloombergUrl = self.url % ticker
		csv_string = urllib2.urlopen(bloombergUrl).read()
		matchObj = re.search(r'\" price\">\s?([0-9],)?.*',csv_string,re.M|re.I)

		if matchObj:
			cdsPrice =  matchObj.group()
			twoMatch = re.search(r'\s[0-9]+\.[0-9]+.*',cdsPrice, re.M|re.I)
			return {'ticker': ticker, 'price':float(twoMatch.group())}
		else:
			return {}



	def fetchHistoricalPrices(self, ticker, daysOffsetStart, daysOffsetEnd):
		endPeriod = date.today() - timedelta(days=daysOffsetEnd)
		startPeriod = date.today() - timedelta(days=daysOffsetStart)
		return self.fetchHistoricalWithDateParams(ticker, startPeriod, endPeriod)

	def fetchHistoricalWithDateParams(self, ticker, startPeriod, endPeriod):

		inputParams = {'ticker' : ticker, 'fromMonth':startPeriod.month-1, 'fromDay': startPeriod.day, 'fromYear':startPeriod.year, 'toDay' : endPeriod.day, 'toMonth':endPeriod.month-1, 'toYear': endPeriod.year}

		

		historicalUrl = self.historicalPricesUrl % (inputParams)

		historicalPrices = []
		for row in  self.csv_worker.openUrl(historicalUrl):
			historicalPrices.append(row[4])

		# skipping headers
		return historicalPrices[1:]



	def lookupTicker(self, companyName):
		replacedName = companyName.replace(' ', '+').replace('\'s','')
		companyTokens = ['Ltd','Limited','Inc','Corp','Corporation','PLC','SpA','Co']
		for token in companyTokens:
			# make Sure token is at the end of the company name
			minIndex = len(replacedName) - len(token) - 2
			if replacedName.rfind(token) > minIndex:
				replacedName = replacedName[0:replacedName.rfind(token)]

		trailingDots = replacedName.rfind(',')
		trailingCommas = replacedName.rfind('.')


		if trailingDots > 0:
			replacedName = replacedName[0:trailingDots]

		if trailingCommas > 0:
			replacedName = replacedName[0:trailingCommas]
		
		f = urllib2.urlopen(self.lookupUrl % (replacedName))
		
		parsedResult = StringIO.StringIO(f.read())

		result = parsedResult.getvalue()
			
		#print result	
		startIdx = result.find('{')
		endIdx = result.rfind('}')

		#print 'company:%s,  urlName:%s' % (companyName, replacedName)

		jsonTree = result[startIdx:endIdx+1]
		
		try:
			s = simplejson.loads(jsonTree)
			tickerList = s['ResultSet']['Result']
			stockData = []	
			for data in tickerList:
				stockData.append((data['symbol'], data['exch']))
				if data['exch'] == 'NYQ' or data['exch'] == 'NAS':
					return (data['symbol'], data['exch'])
			return stockData[0]
		except:
			#print 'error in fetching json data for %s(original), %s(replacedname) ' % (companyName, replacedName)
			#print 'Original:{%s}, Replaced:{%s}' % (companyName, replacedName)			
			return []
		

	def findCompanyForSector(self, sector):
		return 'Not found'



class strategyWorker(object):

	def __init__(self, yahooClient, sqliteConnector):
		self.dbConnector = sqliteConnector
		self.yahoo_client = yahooClient

	def uploadShare(self, jsonDict):
		# get ticker
				
		ticker = jsonDict['ticker']
		name = jsonDict['name']
		logger.info('uploading data for:' + ticker)
		# store sector
		company = self.yahoo_client.lookupTicker(ticker)
		logger.info('Uploded..')
		logger.info('Storing into sectors..')
		newSector = {'name': name, 'ticker':ticker, 'sectorId': -2, 'source': 'yhoo' if ticker.startswith('^') else 'goog'}
		self.dbConnector.storeSector(newSector)
		

	# Fetches the best 5 sectors. And in there, fetches the best companies in the sector
	def fetchBestSectors(self, dictParameters):
		logger.info('finding best sectors..')
		sectorPrices = []
		
		offsetTuple = self._fetchDaysOffset(dictParameters)
		startPeriod = offsetTuple[0]
		endPeriod = offsetTuple[1]
		logger.debug('finding sector data..')		
		sector_data = self.dbConnector.executeSql('select ticker, name, sectorId from sectors ')
		sectorPerformance = []		
		
		for sector in sector_data:
						
			sector_prices = self._fetchPricesForSector(sector[0], startPeriod, endPeriod)
			if len(sector_prices) > 0:
                                tmpPrice = sector_prices[len(sector_prices) -1][1]
                                mostRecentPrice = -1 if tmpPrice == 'N/A' else float(tmpPrice)	
                                #mostRecentPrice = float(sector_prices[len(sector_prices) -1][1])
                                oldestPriceTmp = sector_prices[0][1] if len(sector_prices)> 1 else '1.0'
                                oldestPrice = -1 if oldestPriceTmp == 'N/A' else float(oldestPriceTmp)   
                                #oldestPrice = float(sector_prices[0][1]) if len(sector_prices) > 1 else 1.0
  	                        if oldestPrice == 0.0:
                                        logger.info('Oldest Price is zero for %s', sector[1])
                        		mostRecentPrice, oldestPrice = 0, 1
        	  		sectorPerf = mostRecentPrice / oldestPrice
				#sectorPerf = float(sector_prices[len(sector_prices) - 1][1]) / float( sector_prices[0][1] if len(sector_prices) > 1  else 1)
				sectorPerformance.append((sector[1], sector[2], sectorPerf, sector[0]))
		
		sorted_data = sorted(sectorPerformance, key=lambda sector: sector[2], reverse=True)
		for sector in  sorted_data[0:5]:
			if sector[2] > 0:			
				logger.debug( 'fetching data for all companies of sector:%s{%s}' % (sector[1], sector[0]))
				sectorId = sector[1]
				companies = []
				companies_prices = []
				'''				
				if sectorId > 0:	
					logger.info('sectorId:%s, sectorName:%s' % (sectorId, sector[1]))		
					companies = self.dbConnector.executeSql('select name, ticker from companies where sectorId=\'%s\'' % (sectorId))
					for company in companies:
						try:				
							historical_data = self.yahoo_client.fetchHistoricalWithDateParams(company[1], startPeriod, endPeriod)
							first = historical_data[len(historical_data) -1]
							latest = historical_data[0]
							companies_prices.append((company[0], company[1], float(latest)/float(first), first,latest))
						except:
							logger.error( 'unable to fetch historical data for: %s, %s' % (company[0],company[1])) 
				 
				sorted_company_prices = sorted(companies_prices, key=lambda price:price[2], reverse=True)
				'''
				sorted_company_prices = self._fetchSectorCompanies(sectorId, offsetTuple)
				sectorPrices.append({'sector': sector[0], 'ticker':sector[3], 'performance':sector[2], 'companies': sorted_company_prices[0: 5 if len(sorted_company_prices) > 4 else 4]})

		return sectorPrices


	# Fetches the best 5 sectors. And in there, fetches the best companies in the sector
	def fetchWorstSectorsPerformance(self, dictParameters):
		logger.info('finding worst sectors..')
		sectorPrices = []
		
		offsetTuple = self._fetchDaysOffset(dictParameters)
		startPeriod = offsetTuple[0]
		endPeriod = offsetTuple[1]
		logger.info('finding worst sector data..')		
		sector_data = self.dbConnector.executeSql('select ticker, name, sectorId from sectors ')
		sectorPerformance = []		
		
		for sector in sector_data:
			sector_prices = self._fetchPricesForSector(sector[0], startPeriod, endPeriod)
			if len(sector_prices) > 0:		
				sectorPerf = float(sector_prices[len(sector_prices) - 1][1]) / float( sector_prices[0][1] if len(sector_prices) > 1  else 1)
				sectorPerformance.append((sector[1], sector[2], sectorPerf, sector[0]))
		
		sorted_data = sorted(sectorPerformance, key=lambda sector: sector[2])
		for sector in  sorted_data[0:5]:
			if sector[2] > 0:			
				logger.info( 'fetching data for all companies of sector:%s{%s}' % (sector[1], sector[0]))
				sectorId = sector[1]
				sectorPrices.append({'sector': sector[0], 'ticker':sector[3], 'performance':sector[2]})

		return sectorPrices



	def _fetchDaysOffset(self, dictParameters):
		dayOffsetStart = 91
		dayOffsetEnd = -1
		if 'months' in dictParameters:
			numMonths = dictParameters['months']
			if numMonths == '3':
				dayOffsetStart = 91
				dayOffsetEnd = 1
			elif numMonths == '6':
				dayOffsetStart = 181
				dayOffsetEnd = 1
			elif numMonths == '3/6':
				dayOffsetStart = 181
				dayOffsetEnd = 91
		endPeriod = date.today() - relativedelta(days=dayOffsetEnd)
		startPeriod = date.today() - relativedelta(days=dayOffsetStart)
		
		return (startPeriod, endPeriod)		

	def fetchSectorPerformance(self, jsonParams):
		logger.info('fetching sector performance for',jsonParams)
		offsetTuple = self._fetchDaysOffset(jsonParams)
		sectorTicker = jsonParams['ticker']
		sector_data = self.dbConnector.executeSql('select ticker, name, sectorId from sectors where ticker = ?', [sectorTicker])
		if len(sector_data) > 0:
			sector_prices = self._fetchPricesForSector(sectorTicker, offsetTuple[0], offsetTuple[1])
			if len(sector_prices) > 0:
				sectorPerf = float(sector_prices[len(sector_prices) - 1][1]) / float( sector_prices[0][1] if len(sector_prices) > 1  else 1)
				return {'sectorName': sector_data[0][1], 'ticker':sectorTicker, 'performance': ((sectorPerf-1) * 100),
					'oldestPrice': float( sector_prices[0][1]), 'latestPrice':float(sector_prices[len(sector_prices) - 1][1])}
			return {'Error' : 'No Sector found for:%s' % sectorTicker}
		return {'Error' : 'No Sector found for:%s' % sectorTicker}

	
	def fetchSectorCompanies(self, jsonParams):
		logger.info('fetching sector companies for',jsonParams)
		offsetTuple = self._fetchDaysOffset(jsonParams)
		sectorTicker = jsonParams['ticker']
		sector_data = self.dbConnector.executeSql('select sectorId from sectors where ticker = ?', [sectorTicker])
		if sector_data[0][0] > 0:
			return self._fetchSectorCompanies(sector_data[0][0], offsetTuple)

	def _fetchSectorCompanies(self, sectorId,offsetTuple):
		logger.info('Fetching companies for:' + sectorId)
		start = offsetTuple[0]
		end = offsetTuple[1]
		companies_prices = []
		if sectorId > 0:	
			companies = self.dbConnector.executeSql('select name, ticker from companies where sectorId=\'%s\'' % (sectorId))
			for company in companies:
				try:				
					historical_data = self.yahoo_client.fetchHistoricalWithDateParams(company[1], start, end)
					first = historical_data[len(historical_data) -1]
					latest = historical_data[0]
					companies_prices.append({'name':company[0], 'ticker':company[1], 'performance':float(latest)/float(first), 'oldest_price':first,'newest_price':latest})
				except:
					logger.error( 'unable to fetch historical data for: %s, %s' % (company[0],company[1])) 
	 		sorted_company_prices = sorted(companies_prices, key=lambda price:price['performance'], reverse=True)
			listLen = len(sorted_company_prices) if len(sorted_company_prices) < 5 else 5		
			return sorted_company_prices[0:listLen]
		else:
			return []
		

	def _fetchPricesForSector(self,sectorTicker, startPeriod, endPeriod):
		logger.debug('finding data for %s, %s, %s:' % (sectorTicker, startPeriod.strftime('%Y-%m-%d'), endPeriod.strftime('%Y-%m-%d')))
		sector_prices = self.dbConnector.executeSql('select ticker , price, asofdate from stock_prices where ticker=\'%s\' and asofdate >=  date(\'%s\') and asofdate <= \'%s\''  % (sectorTicker, startPeriod.strftime('%Y-%m-%d'), endPeriod.strftime('%Y-%m-%d')))
		logger.debug( 'found:%s prices for:%s' % (len(sector_prices), sectorTicker))	
		return sector_prices	

	def fetchTopSectors(self, startOffset, endOffset):
		logger.info( 'fetching top sectors')
		sectorData = self.dbConnector.executeSql('select ticker , price from stock_prices')
		sectorPrices = []
		sorted_data = sorted(sectorData, key=lambda sector: sector[1], reverse=True)
		for sector in  sorted_data[0:5]:
			sector_data = self.dbConnector.executeSql('select name, sectorId from sectors where ticker=\'%s\'' % (sector[0]))
			sectorId = sector_data[0][1]			
			companies = self.dbConnector.executeSql('select name, ticker from companies where sectorId=\'%s\'' % (sectorId))
			companies_prices = []

			for company in companies:
				try:				
					historical_data = self.yahoo_client.fetchHistoricalPrices(company[1], startOffset, endOffset)
					latest = historical_data[len(historical_data) -1]
					first = historical_data[0]
					companies_prices.append((company[0], company[1], float(latest)/float(first)))
				except:
					print 'unable to fetch historical data for:',company[0],company[1] 
				 
			sorted_company_prices = sorted(companies_prices, key=lambda price:price[2], reverse=True)
		
			sectorPrices.append({'sector': sector_data[0][0], 'companies': sorted_company_prices[0: 5 if len(sorted_company_prices) > 4 else 4]})

		logger.info('returning:\n%s' % (sectorPrices))	
		return sectorPrices


		# What we should do here:
		# 1 - fetch each sector		
		# 2 - fetch Sector Data prices, for the last 3 months
		# 3 - find out which 5 sectors performed best in last 3 months
		# 4 - for each of the top 5 sectors, fetch historical prices for each company in the sector
		# 4.1 sort out best 5 shares
		# 5 return, for each sector, 
		#    sectorTicker, sectorName, return
		#     company1, ticker, return
		# SOMEHOW we'll need to find out how to compare return of last 3 months vs returns of last 6 months
		# Possible solution: we send two parameters to the strategyWorker. if the 2nd parameter is present, we
		# fetch data for 3months and 6 months. 
		# otherwise, we fetch data for 3 months


	



######## SECTOR FINDER ##############################
# This class loades data for each sector.
# This class should refresh the list of sector/companies on a monthly basis
# so that we can pick the latest fund. analysis parameters
class sectorFinder(object):

	def __init__(self, yahoo_client, dbConnector):
		self.yahooClient = yahoo_client
		self.sqliteConnector = dbConnector
		self.sched = Scheduler()
		self.sched.start()
		logger.info( 'scheduling')
		self.sched.add_cron_job(self.fetchData, day_of_week='mon-sun', hour=19, minute=20)
		self.sched.add_cron_job(self.loadData,month='1-12', day='last sun', hour='1')
		logger.info('scheduling export prices...')
                self.sched.add_cron_job(self.exportPrices,day_of_week='sat', hour=10, minute=00)
		self.initialise()
		

	def initialise(self):
		result = self.sqliteConnector.executeSql('select count(*) from sectors')
		if result[0][0] < 1:
			logger.info( 'loading data..')
			self.loadData()				


	def findSectorData(self, sectorId):
		logger.info( 'Finding sector index for:%s' % sectorId)
		sectorPage = 'http://biz.yahoo.com/ic/%s.html'
		
		sectorIdx = '(^YHO'

		# here we need to find the sectorId
		data = urllib2.urlopen(sectorPage % sectorId)
				
		for line in data.readlines():
			idx = line.find(sectorIdx)
			if idx > 0:
				# ok we got it, now narrowing it down
				return {'name': line[line.find('[') +1:line.find('(')], 'ticker': line[line.find('(')+1: line.find(')')],
					'sectorId':sectorId, 'source':'yhoo'}
		return None

	def findAllSectors(self) :
		logger.info( 'attempting to find all sectors' )
		data = urllib2.urlopen('http://biz.yahoo.com/ic/ind_index_alpha.html')
		sectorNumList = []
		for line in data.readlines():
			idx = line.find('http://biz.yahoo.com/ic/')

			htmlIdx = line.find('.html')
			if  idx > 0 and htmlIdx > 0:
				subString = line[idx:htmlIdx]
				sectorNumIdx = subString.rfind('/')		
				yahooSector  = subString[sectorNumIdx+1:len(subString)]
				sectorNumList.append(yahooSector)
		return sectorNumList[3:]

	def findAllSectorShares(self, sectorId):
		logger.info( 'finding all shares for:%s' % sectorId)
		allSectorStocks = 'http://biz.yahoo.com/p/csv/%sconameu.csv'

		allCompaniesNames = []		
		f = urllib2.urlopen(allSectorStocks % sectorId)

		reader = csv.reader( (line.replace('\0','') for line in f),delimiter=',')
		#reader = csv.reader(f)

		for line in reader:
			if len(line) > 0:
				# Note: this line contains 1Day price change, MarketCap, P/E, ROE, DIV YIeld, Debt to Equity, PriceToBook,NetProfitMargin PriceToFree Cash Flow
				allCompaniesNames.append({'Description':line[0], 'PriceChange': line[1], 'MarketCap':line[2], 'PriceToEarnings': line[3], 'Roe': line[4], 'DivYield':line[5], 'LtDToEq': line[6], 'PriceToBook':line[7], 'NetProfMgn' : line[8], 'PriceToCashFlow': line[9], 'sectorId' :sectorId})

		return allCompaniesNames[3:]


	def findSectorCompanyTickers(self,company):
		if company['PriceChange'] != 'NA':
			ticker =  self.yahooClient.lookupTicker(company['Description'])
			#print 'Obtained:',ticker
			
			if len(ticker) > 0:
				company['ticker'] = ticker[0] if len(ticker) > 0 else ''
				return (company, ticker[0] if len(ticker) > 0 else '')
			else:
				return [company, []]
		else:
			return (company, 'Ticker not available') 			
		#return alldata

	def loadData(self) :
		logger.info( 'loading data.....')
		allDataCount = 0
		allMissingCount = 0
		f = open('results.txt', 'w')
	
		storedCount = 0;

		for sector in self.findAllSectors():
			missingCompaniesTicker = 0
			sectorData = self.findSectorData(sector)
			self.sqliteConnector.storeSector(sectorData)

			f.write('########################### SECTOR:%s,  TICKER:%s ' %(sectorData['name'], sectorData['ticker']))
			f.write('\n')		
			
			companies = self.findAllSectorShares(sectorData['sectorId'])
			allDataCount += len(companies)
			for company in companies:	
				companyData = self.findSectorCompanyTickers(company)
				#sFinder.findSectorCompanyTickers(company)
				if len(companyData[1]) == 0 or companyData[1] == 'Ticker not available':
					missingCompaniesTicker +=1
					#f.write(str(company));
					#f.write('\n')
				else:
					self.sqliteConnector.storeCompanies(companyData[0])
					storedCount +=1
			allMissingCount += missingCompaniesTicker	
			f.write('----- Out of %s companies in the sector,  %s  are missing....\n' % (len(companies), missingCompaniesTicker))
			f.write('\n')
		f.write('**************************Stored Exactly:%s companies' % (storedCount))		
		f.close()
		logger.info( 'Stored exactly:%s companies' % (storedCount))		
		
        def exportPrices(self):
		logger.info('exporting prices.........')
		self.sqliteConnector.exportPrices()

	def fetchData(self):
		logger.info( 'loading what\'s existing')
		companies =  self.sqliteConnector.executeSql('select ticker from companies')
		logger.info( 'loaded %s companies' % (len(companies)))
		sectors =  self.sqliteConnector.executeSql('select name,ticker, sectorId,source from sectors')
		logger.info('loaded %s sectors' % (len(sectors)))
		logger.info('loading shares')
		shares = self.sqliteConnector.executeSql('select ticker from stock_prices')
		logger.info('loaded %s shares' % (len(shares)))
		logger.info('populating shares for first 5 sectors')

		for sector in sectors:
			logger.info('loading stock prices for:%s' % sector[0])
			stock_price = {}			
			if sector[3] == 'yhoo':			
				stock_price = self.yahooClient.fetchDataFromYahoo(sector[1])
			else:
				stock_price = self.yahooClient.get_quote(sector[1])

			if len(stock_price) > 0:
                                from datetime import date
				dateAsOfDate = datetime.strptime(stock_price['asofdate'], '%m/%d/%Y') if stock_price['asofdate'] != 'N/A' else date.today()
				stock_price['asofdate'] = dateAsOfDate.strftime('%Y-%m-%d %H:%M:%S')
						
				logger.info( 'storing stock price for sector..')
				logger.info(stock_price)			
				self.sqliteConnector.store(stock_price)
			else:
				print 'Could not get data for:%s' % (sector[1]) 
			''' Skipping for the moment shares, at it leads
			'' to  URL open exception  due to the high number of calls			
			sectorCompanies = self.sqliteConnector.executeSql('select ticker, name from companies where sectorId = \'%s\'' % (sector[2]))
			for company in sectorCompanies:
			company_stock = self.yahooClient.fetchDataFromYahoo(company[0])
			print 'storing stock price for :%s' % (company[1])
			self.sqliteConnector.store(company_stock)	
			'''
if __name__ =='__main__':
	yahoo_client = yahooClient()
	dbConnector = sqliteConnector()
	sFinder = sectorFinder(yahoo_client, dbConnector)
	strategy = strategyWorker(yahoo_client, dbConnector)
	
	'''
	data = sFinder.findAllSectorShares('712')

	for c in data:
		if c['Description'].startswith('McD'):
			print c['Description']
			print 'finding ticker now...'
			print yahoo_client.lookupTicker(c['Description'])
	'''
	
	
	if sys.argv[1] == 'load':
		#		print 'loaded:%s, missing:%s' % (allDataCount, allMissingCount)
		print 'loading data'		
		sFinder.loadData()
		#print sFinder.findSectorCompanyTickers(companies)
	elif sys.argv[1] == 'fetch':
		sFinder.fetchData()
		print '##############fetching best perf sectors  ####################' 
				
		dictParams = {}		
		if len(sys.argv) > 2:
			dictParams['months'] = sys.argv[2]
		
		print 'calling with dictParams',dictParams
		for perfSector in strategy.fetchBestSectors(dictParams):
		#fetchTopSectors(91,1):
			print perfSector
		
	else:
		print 'initalising sFinder..'
		sFinder.initialise()
		
	

#print sectorNumList		
