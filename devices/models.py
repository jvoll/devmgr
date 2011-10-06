from django.db import models

class Device(models.Model):
    name = models.CharField(max_length=150)
    latitude = models.DecimalField(max_digits=9, decimal_places=6)
    longitude = models.DecimalField(max_digits=9, decimal_places=6)
    allow_tracking = models.BooleanField()
    is_wiped = models.BooleanField()
    wipe_requested = models.BooleanField()
    google_id = models.CharField(max_length=255)

    def __unicode__(self):
	return self.name

    def to_dict(self):
	dict = {
	    'id': self.id,
	    'name': self.name,
	    'latitude': self.latitude,
	    'longitude': self.longitude,
	    'allow_tracking': self.allow_tracking,
	    'is_wiped': self.is_wiped,
	    'wipe_requested': self.wipe_requested,
	    'google_id': self.google_id
	}
	return dict
