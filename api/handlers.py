from piston.handler import AnonymousBaseHandler
from devmgr.devices.models import Device
from piston.utils import rc, require_mime
from devmgr.c2dm.c2dm_sender import C2DMSender
import json

# Handler for registering new devices
# and getting general info on the registered devices
class DeviceHandler(AnonymousBaseHandler):
    allowed_methods = ('GET','POST')
    model = Device

    # Gets an individual device or a list of all of them
    def read(self, request, device_id=None):
	if device_id:
	    try:
		ret = Device.objects.get(pk=device_id).to_dict()
	    except Device.DoesNotExist:
		return rc.NOT_FOUND
	    return ret
	else:
	    ret = []
	    for device in Device.objects.all():
		ret.append(device.to_dict())
	    return ret

    # Add a device
    def create(self, request):
	j = json.loads(request.raw_post_data)

	if 'name' in j:
	    new_device = Device(name=j['name'], latitude=0.0, longitude=0.0,
			allow_tracking=False, is_wiped=False,
			wipe_requested=False, track_frequency=3600,
			loc_timestamp=0)
	    new_device.save()
	    resp=rc.CREATED
	    resp.content={'id':new_device.id}
	    return resp
	else:
	    return rc.BAD_REQUEST

# Used to handle the update of a google c2dm id
class DeviceC2DMRegisterHandler(AnonymousBaseHandler):
    allowed_methods = ('GET', 'PUT')

    # Get the Google registration ID for a device
    def read(self, request, device_id):
	try:
	    device = Device.objects.get(pk=device_id)
	except Device.DoesNotExist:
	    return rc.NOT_FOUND

	return {'google_id':device.google_id}

    # Add a Google c2dm id to a device
    def update(self, request, device_id):
	try:
	    device = Device.objects.get(pk=device_id)
	except Device.DoesNotExist:
	    return rc.NOT_FOUND

	j = json.loads(request.raw_post_data)

	device.google_id = j['google_id']
	device.save()
	return rc.ALL_OK

# handler to send a c2dm message
class C2DMSendHandler(AnonymousBaseHandler):
    allowed_methods=('POST')

    # Send a push notification to a device
    def create(self, request, device_id):
	print "got api to send c2dm message"
	try:
	    device = Device.objects.get(pk=device_id)
	except Device.DoesNotExist:
	    return rc.NOT_FOUND

	success = C2DMSender().send_push_msg(device_id, "push msg from api")

	if success:
	    resp = rc.CREATED
	else:
	    resp = rc.INTERNAL_ERROR
	return resp

# Used to get/set device location information
class DeviceLocationHandler(AnonymousBaseHandler):
    allowed_methods = ('GET','PUT')
    model = Device

    def read(self, request, device_id):
	try:
	    device = Device.objects.get(pk=device_id)
	except Device.DoesNotExist:
	    return rc.NOT_FOUND

	return {'id':device.id, 'latitude':device.latitude, 'longitude':device.longitude,
		'loc_timestamp':device.loc_timestamp }

    def update(self, request, device_id):
	try:
	    device = Device.objects.get(pk=device_id)
	except Device.DoesNotExist:
	    return rc.NOT_FOUND

	j = json.loads(request.raw_post_data)

	# update location if all necessary pieces included
	if 'latitude' in j and 'longitude' in j and 'loc_timestamp' in j:
	    device.latitude = j['latitude']
	    device.longitude = j['longitude']
	    device.loc_timestamp = j['loc_timestamp']
	    device.save()
	    return rc.ALL_OK
	else:
	    return rc.BAD_REQUEST

# API for allowing a device to be tracked
class DeviceAllowTrackHandler(AnonymousBaseHandler):
    allowed_methods = ('GET','PUT')
    model = Device

    def read(self, request, device_id):
	try:
	    device = Device.objects.get(pk=device_id)
	except Device.DoesNotExist:
	    return rc.NOT_FOUND

	return {'id':device.id,	'allow_tracking':device.allow_tracking}

    def update(self, request, device_id):
	try:
	    device = Device.objects.get(pk=device_id)
	except Device.DoesNotExist:
	    return rc.NOT_FOUND

	j = json.loads(request.raw_post_data)

	# update allow_tracking if included
	if 'allow_tracking' in j:
	    tracking = string_to_bool(j['allow_tracking'])
	    device.allow_tracking = tracking

	    # erase stored tracking information if disabiling tracking
	    if not tracking:
		device.latitude = 0.0
		device.longitude = 0.0
		device.loc_timestamp = 0

	    device.save()
	    return rc.ALL_OK
	else:
	    # if not included, return an error
	    return rc.BAD_REQUEST

class DeviceWipeHandler(AnonymousBaseHandler):
    allowed_methods = ('GET', 'PUT')
    model = Device

    def read(self, request, device_id):
	try:
	    device = Device.objects.get(pk=device_id)
	except Device.DoesNotExist:
	    return rc.NOT_FOUND

	return {'id':device.id, 'wipe_requested':device.wipe_requested,
		'is_wiped':device.is_wiped}

    def update(self, request, device_id):
	try:
	    device = Device.objects.get(pk=device_id)
	except Device.DoesNotExist:
	    return rc.NOT_FOUND

	j = json.loads(request.raw_post_data)

	# update wipe_requested if included
	if 'wipe_requested' in j:
	    device.wipe_requested = string_to_bool(j['wipe_requested'])

	# update is_wiped if included
	if 'is_wiped' in j:
	    device.is_wiped = string_to_bool(j['is_wiped'])

	device.save()
	return rc.ALL_OK

# Location update frequency in seconds
class LocFrequencyHandler(AnonymousBaseHandler):
    allowed_methods = ('GET', 'PUT')
    model = Device

    def read(self, requence, device_id):
	try:
	    device = Device.objects.get(pk=device_id)
	except Device.DoesNotExist:
	    return rc.NOT_FOUND

	return {'id':device.id, 'track_frequency':device.track_frequency}

    def update(self, request, device_id):
	try:
	    device = Device.objects.get(pk=device_id)
	except Device.DoesNotExist:
	    return rc.NOT_FOUND

	j = json.loads(request.raw_post_data)

	if 'track_frequency' in j:
	    device.track_frequency = j['track_frequency']
	    device.save()
	    resp = rc.ALL_OK
	else:
	    resp = rc.BAD_REQUEST

	return resp

# helper
def string_to_bool(text):
    return {"true": True, "false": False}.get(text.lower())
