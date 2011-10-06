import urllib
import urllib2

class ClientLoginTokenFactory():
    _token = None

    def __init__(self):
        self.url = 'https://www.google.com/accounts/ClientLogin'
        self.account_type = 'HOSTED_OR_GOOGLE'
        self.email = 'jvoll@mozilla.com'
        self.password = 'm0ziLL@devmgr'
        self.source = 'MOZILLA-DM-V0.1'
        self.service = 'ac2dm'

    def get_token(self):
        if(self._token is None):
	    # build payload
            values = {'accountType' : self.account_type,
                      'Email' : self.email,
                      'Passwd' : self.password,
                      'source' : self.source,
                      'service' : self.service}

	    # build request
            data = urllib.urlencode(values)
            request = urllib2.Request(self.url, data)

	    # post
            response = urllib2.urlopen(request)
            responseAsString = response.read()

	    # format response
            responseAsList = responseAsString.split('\n')

            self._token = responseAsList[2].split('=')[1]

        return self._token
