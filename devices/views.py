from django.shortcuts import render_to_response, get_object_or_404
from devices.models import Device
from django.template import RequestContext
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse
from devmgr.c2dm.c2dm_sender import C2DMSender

def index(request):
    device_list = Device.objects.all().order_by('-id')[:5]
    return render_to_response('devices/index.html', {'device_list': device_list},
				context_instance=RequestContext(request))

# TODO:
# 1. Find a way to ensure that this is a post request
# 2. Find a way to make web portal and api call the same code (is this
#    actually necessary since the wbsite and the api will be doing different
#    things and probably never making the same call? Maybe they will share some
#    calls though:S think!!!
def wipe(request, device_id):
    device = get_object_or_404(Device, pk=device_id)
    device.wipe_requested = True
    device.save()
    return HttpResponseRedirect(reverse('devices.views.index'))


def send_c2dm(request, device_id):
    print "Sending push notification to device %s" % device_id
    success = C2DMSender().send_push_msg(device_id, "sent msg from the website")
    print success
    return HttpResponseRedirect(reverse('devices.views.index'))
