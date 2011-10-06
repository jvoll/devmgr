from django.conf.urls.defaults import *
from piston.resource import Resource
from devmgr.api.handlers import *

# TODO: CSRF protection currently disabled...fix this!
# The below is stolen from Taedium, maybe I can use that
"""
class CSRFDisabledResource(Resource):
    def __init__(self, **kwargs):
	super(self.__class>>, self).__init__(**kwargs)
	self.csrf_exempt = getattr(self.handler, 'csrf_exmpt', True)

#Uses Django authentication by default
auth = HttpBasicAuthentication()
"""
#device_handler = CSRFDisabledResource(handler=DeviceHandler, authentication=auth)

device_handler = Resource(DeviceHandler)
device_location_handler = Resource(DeviceLocationHandler)
device_wipe_handler= Resource(DeviceWipeHandler)
device_c2dm_register_handler = Resource(DeviceC2DMRegisterHandler)
device_c2dm_send_handler = Resource(C2DMSendHandler)

urlpatterns = patterns('',
    (r'^devices/register$', device_handler),
    (r'^devices/(?P<device_id>\d+)$', device_handler),
    (r'^devices/$', device_handler),
    (r'^devices/(?P<device_id>\d+)/location$', device_location_handler),
    (r'^devices/(?P<device_id>\d+)/wipestatus$', device_wipe_handler),
    (r'^devices/c2dm/(?P<device_id>\d+)/register$', device_c2dm_register_handler),
    (r'^devices/c2dm/(?P<device_id>\d+)/send$', device_c2dm_send_handler),
)
