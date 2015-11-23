/*

Copyright (c) 2015 Tuo Zhang tzhang05@ca.ibm.com
Copyright (c) 2015 IBM

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

*/

import UIKit

class ViewController: UIViewController {

    let url = "http://db2iossample132.mybluemix.net/"
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }

    //check the Internet connection; replace the server name with your server within the url
    @IBAction func connection_test(sender: AnyObject) {
        NSURLConnection.sendAsynchronousRequest(NSURLRequest(URL: NSURL(string: url)!), queue: NSOperationQueue()) {(resp: NSURLResponse?, data:NSData?, error: NSError?) -> Void in
            if let test_result_data = data {
                dispatch_sync(dispatch_get_main_queue()
                    ,{ () -> Void in
                        self.messageout.text = String("Connection Successful.")
                })
            }
            else {
                dispatch_sync(dispatch_get_main_queue()
                    ,{ () -> Void in
                        self.messageout.text = String("Connection Failed.")
                })
            }
        }//connection request
    }//connection_test
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    //Delete a specific item in the database; replace the server name with your server within the url
    @IBAction func delete_action(sender: AnyObject) {
        NSURLConnection.sendAsynchronousRequest(NSURLRequest(URL: NSURL(string: url + "deleteItem?item=\(self.messagein_delete.text!)")!), queue: NSOperationQueue()) {(resp: NSURLResponse?, data:NSData?, error: NSError?) -> Void in
            if let test_result_data = data {
                dispatch_sync(dispatch_get_main_queue()
                    ,{ () -> Void in
                        self.messageout.text = String("Delete Request Sent.")
                })
            }
            else {
                dispatch_sync(dispatch_get_main_queue()
                    ,{ () -> Void in
                        self.messageout.text = String("Connection Error.")
                })
            }
        }//connection request
    }//delete_action
    
    //Select all items in the database; replace the server name with your server within the url
    @IBAction func select_all(sender: AnyObject) {
        NSURLConnection.sendAsynchronousRequest(NSURLRequest(URL: NSURL(string: url + "items")!), queue: NSOperationQueue()) {(resp: NSURLResponse?, data:NSData?, error: NSError?) -> Void in
            if let test_result_data = data {
                dispatch_sync(dispatch_get_main_queue()
                    ,{ () -> Void in
                        self.messageout.text = NSString(data: test_result_data, encoding: NSUTF8StringEncoding) as! String
                })
            }
            else {
                dispatch_sync(dispatch_get_main_queue()
                    ,{ () -> Void in
                        self.messageout.text = String("Loading Failed.")
                })
            }
        }//connection request
    }//select_all
    
    //Insert a specific item into the database; replace the server name with your server within the url
    @IBAction func insert(sender: AnyObject) {
        NSURLConnection.sendAsynchronousRequest(NSURLRequest(URL: NSURL(string: url + "addItem?item=\(self.messagein_insert.text!)")!), queue: NSOperationQueue()) {(resp: NSURLResponse?, data:NSData?, error: NSError?) -> Void in
            if let test_result_data = data {
                dispatch_sync(dispatch_get_main_queue()
                    ,{ () -> Void in
                        self.messageout.text = String("Insert Request Sent.")
                })
            }
            else {
                dispatch_sync(dispatch_get_main_queue()
                    ,{ () -> Void in
                        self.messageout.text = String("Connection Error.")
                })
            }
        }//connection request
    }//insert
    
    //Input and Output views
    @IBOutlet weak var messageout: UITextView!
    @IBOutlet weak var messagein_insert: UITextField!
    @IBOutlet weak var messagein_delete: UITextField!
}//ViewController

