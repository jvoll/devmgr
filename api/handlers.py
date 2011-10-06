from piston.handler import AnonymousBaseHandler
from devmgr.devices.models import Device
from piston.utils import rc, require_mime
from devmgr.c2dm.c2dm_sender import C2DMSender
import json

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
    # TODO remove allow_tracking from this API since not used
    # /api/devices/register json: name, allow_tracking
    def create(self, request):
	j = json.loads(request.raw_post_data)

	tracking = string_to_bool(j['allow_tracking'])

	new_device = Device(name=j['name'], latitude=0.0, longitude=0.0,
			    allow_tracking=tracking, is_wiped=False, wipe_requested=False)

	new_device.save()
	resp=rc.CREATED
	resp.content={'id':new_device.id}
	return resp

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
	print "tetest"
	try:
	    device = Device.objects.get(pk=device_id)
	except Device.DoesNotExist:
	    return rc.NOT_FOUND

	print "got me a device!"
	print device

	j = json.loads(request.raw_post_data)

	print "google id: %s" % j['google_id']
	device.google_id = j['google_id']
	device.save()
	resp = rc.ALL_OK
	return resp

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
# (including whether location is tracked or not)
# TODO create a separate call for updating allow track
# reasoning: update to location will be frequent, update allow will be infrequent
# therefore shouldn't clump their logic together
# also...we are NEVER calling them together from the android app
class DeviceLocationHandler(AnonymousBaseHandler):
    allowed_methods = ('GET','PUT')
    model = Device

    def read(self, request, device_id):
	try:
	    device = Device.objects.get(pk=device_id)
	except Device.DoesNotExist:
	    return rc.NOT_FOUND

	return {'id':device.id, 'latitude':device.latitude, 'longitude':device.longitude,
		'allow_tracking':device.allow_tracking}

    def update(self, request, device_id):
	print "testing location api"
	try:
	    device = Device.objects.get(pk=device_id)
	except Device.DoesNotExist:
	    return rc.NOT_FOUND

	print "found device"
	j = json.loads(request.raw_post_data)

	print "got request data"

	# update allow_tracking if included
	if 'allow_tracking' in j:
	    print "setting allow track"
	    tracking = string_to_bool(j['allow_tracking'])
	    device.allow_tracking = tracking
	    print "set complete"

	# update latitude if included
	if 'latitude' in j:
	    device.latitude = j['latitude']

	# update longitude if included
	if 'longitude' in j:
	    device.longitude = j['longitude']

	print "saving"

	device.save()
	resp = rc.ALL_OK
	return resp

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
	resp = rc.ALL_OK
	return resp

# helper
def string_to_bool(text):
    return {"true": True, "false": False}.get(text.lower())
