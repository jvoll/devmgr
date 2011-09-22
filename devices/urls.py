from django.conf.urls.defaults import *

urlpatterns = patterns('devices.views',
    (r'^$', 'index'),
    (r'^(?P<device_id>\d+)/wipe/$', 'wipe'),
)
