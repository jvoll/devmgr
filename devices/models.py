from django.db import models
from datetime import datetime

class Device(models.Model):
    name = models.CharField(max_length=150)
    latitude = models.DecimalField(max_digits=9, decimal_places=6)
    longitude = models.DecimalField(max_digits=9, decimal_places=6)
    allow_tracking = models.BooleanField()
    track_frequency = models.IntegerField()
    is_wiped = models.BooleanField()
    wipe_requested = models.BooleanField()
    google_id = models.CharField(max_length=255)
    loc_timestamp = models.BigIntegerField()

    def __unicode__(self):
	return self.name

    def to_dict(self):
	dict = {
	    'id': self.id,
	    'name': self.name,
	    'latitude': self.latitude,
	    'longitude': self.longitude,
	    'allow_tracking': self.allow_tracking,
	    'track_frequency': self.track_frequency,
	    'is_wiped': self.is_wiped,
	    'wipe_requested': self.wipe_requested,
	    'google_id': self.google_id,
	    'loc_timestamp': self.loc_timestamp
	}
	return dict

    def getFormattedTimestamp(self):
	return datetime.utcfromtimestamp(self.loc_timestamp/1000)

