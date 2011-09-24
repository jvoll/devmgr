from django.conf.urls.defaults import *

from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    (r'^devices/', include('devices.urls')),
    (r'^admin/', include(admin.site.urls)),
    (r'^api/', include('devmgr.api.urls')),
)

