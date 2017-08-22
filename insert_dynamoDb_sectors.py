'''
Created on 7 Aug 2017

@author: marco
'''
import boto3

def connectToDb(hostName):
    dynamodb = boto3.resource(
    'dynamodb',
    endpoint_url=hostName,
    region_name='dummy_region',
    aws_access_key_id='dummy_access_key',
    aws_secret_access_key='dummy_secret_key',
    verify=False)
    return dynamodb

def createTable(dynamodb, tableName):
    table_name = 'sectors'
    
    try :
      result = dynamodb.create_table(
        TableName=tableName,
          KeySchema=[
            {
                'AttributeName': 'sectorId',
                'KeyType': 'HASH'  # Partition key
            },
            
          ],
          AttributeDefinitions=[
            {
                'AttributeName': 'sectorId',
                'AttributeType': 'N'
            },
            
          ],
          ProvisionedThroughput={
            'ReadCapacityUnits': 10,
            'WriteCapacityUnits': 10
          }
        )
      return result
    except Exception as e:
      print 'Exception %s' % str(e)
    


def populateTable(dynamoTbl, fileName):
    print 'Loading sectors from %s' % fileName
    import csv
    with open('c:/Users/marco/MarketSectorGauge/sectors.csv', 'r') as csvfile:
        reader = csv.reader(csvfile)
        for row in reader:
            sectorName, sectorTicker, sectorId, source = row
            dynamoTbl.put_item(
                    Item={'sectorId': int(sectorId), 
                         'ticker':sectorTicker, 
                          'sectorName' : sectorName, 
                          'source':source})

if __name__ == '__main__':
    import sys
    if len(sys.argv) < 2:
        print 'Usage: python insertSectors.py <filename> <hostname>'
        sys.exit(0)
        
    print '-------------'
    print 'Filename:%s' % sys.argv[1]
    print 'Host:%s' % sys.argv[2]   
    print '------------'  
    dynamodb = connectToDb(sys.argv[2])
    print ('got resource:', dynamodb)
    print('adding table')
    result = createTable(dynamodb, 'sectors')

    print('created table:', result)

    print('getting table')

    table = dynamodb.Table('sectors')

    print('got table:', table)
    
    populateTable(table, sys.argv[1])