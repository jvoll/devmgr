from devmgr.devices.models import Device
from piston.utils import rc
from client_token_factory import ClientLoginTokenFactory
import urllib
import urllib2

# handler to send a c2dm message
class C2DMSender():

    def __init__(self, collaps_key = 'boguskey'):
        self.url = 'https://android.apis.google.com/c2dm/send'
        self._collaps_key = collaps_key
        self.token_factory = ClientLoginTokenFactory()

    # Send a push notification to the device
    def send_push_msg(self, device_id, message="Test c2dm message"):
	print "sending a c2dm msg"
	try:
	    device = Device.objects.get(pk=device_id)
	except Device.DoesNotExist:
	    print "ERROR: device with id %s not found" % device_id
	    return False

	registration_id = device.google_id

	print "got goog id: %s" % registration_id

        values = {
            'collapse_key' : self._collaps_key,
            'registration_id' : registration_id,
	    'data.payload' : message,
        }
        body = urllib.urlencode(values)
        request = urllib2.Request(self.url, body)
        request.add_header('Authorization', 'GoogleLogin auth=' + self.token_factory.get_token())
        response = urllib2.urlopen(request)
        if(response.code == 200):
            print('Attempted to send message to device with registraion id:')
            print(registration_id)
            print('was successfull.')
            print('The body returned is:')
            print(response.read())
            return True
	else:
	    print "Request failed %d" % response.code
	    return False
