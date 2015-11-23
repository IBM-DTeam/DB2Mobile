# Imports
import os  
from flask import Flask, redirect, request  
import urllib  
import datetime  
import json  
import ibm_db    

app = Flask(__name__)

# Get service information if on Bluemix  
if 'VCAP_SERVICES' in os.environ:  
    db2info = json.loads(os.environ['VCAP_SERVICES'])['sqldb'][0]  
    db2cred = db2info["credentials"]  
   
# Connect to DB2  
db2conn = ibm_db.connect("DATABASE="+db2cred['db']+";HOSTNAME="+db2cred['hostname']+";PORT="+str(db2cred['port'])+";UID="+db2cred['username']+";PWD="+db2cred['password']+";","","")  
   
# Main page
@app.route('/')  
def index():
    page = '<title>DB2 Tutorial</title>'  
    page += '<h1>Navigate to <a href="/items">/items</a> to list all the items.</h1>'
    page += '<h1>You can add new items by going to /addItem?item=itemNameToAdd</h1>'
    page += '<h1>You can delete items by going to /deleteItem?item=itemNameToDelete</h1>'

    # Sends the string in page to the client
    return page

# Page that gets the items
@app.route('/items', methods=['GET'])
def items():

    page = ''
  
    if db2conn:
        stmt = ibm_db.exec_immediate(db2conn, "SELECT * FROM " + db2cred['username'] + ".ITEMS")
        # Look through each row, while there is a row
        while ibm_db.fetch_row(stmt) != False:
            # Get the rows Item column value
            page += ibm_db.result(stmt, "ITEM") + '\n'
            
    return page

# Page that adds new items to DB2
@app.route('/addItem', methods=['GET'])
def addItem():

    page = ''

    if db2conn:
        #Get the value of the key 'item' in the query string
        value = request.args.get('item')

        #Execute the insert statement
        ibm_db.exec_immediate(db2conn, "INSERT INTO ITEMS(ITEM) VALUES ('" + value + "')")
        page = 'Added: ' + value
    else:
        page = 'Cannot add item'

    return page

# Page that deletes items that are in DB2
@app.route('/deleteItem', methods=['GET'])
def deleteItem():
    page = ''

    if db2conn:
        value = request.args.get('item')
        ibm_db.exec_immediate(db2conn, "DELETE FROM ITEMS WHERE item='" + value + "'")
        page = 'Deleted: ' + value
    else:
        page = 'Cannot delete item'

    return page
   
port = os.getenv('VCAP_APP_PORT', '5000')  
if __name__ == "__main__":  
    app.run(host='0.0.0.0', port=int(port), debug=True)  



