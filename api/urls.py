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
device_allow_track_handler = Resource(DeviceAllowTrackHandler)
device_wipe_handler= Resource(DeviceWipeHandler)
device_c2dm_register_handler = Resource(DeviceC2DMRegisterHandler)
device_c2dm_send_handler = Resource(C2DMSendHandler)
device_loc_frequency_handler = Resource(LocFrequencyHandler)

urlpatterns = patterns('',
    (r'^register$', device_handler),
    (r'^(?P<device_id>\d+)$', device_handler),
    (r'^$', device_handler),
    (r'^(?P<device_id>\d+)/location$', device_location_handler),
    (r'^(?P<device_id>\d+)/allowtrack$', device_allow_track_handler),
    (r'^(?P<device_id>\d+)/wipestatus$', device_wipe_handler),
    (r'^c2dm/(?P<device_id>\d+)/register$', device_c2dm_register_handler),
    (r'^c2dm/(?P<device_id>\d+)/send$', device_c2dm_send_handler),
    (r'^(?P<device_id>\d+)/trackfrequency$', device_loc_frequency_handler),
)
