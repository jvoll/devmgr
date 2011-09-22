from django.db import models

class Device(models.Model):
    name = models.CharField(max_length=150)
    latitude = models.DecimalField(max_digits=7, decimal_places=4)
    longitude = models.DecimalField(max_digits=7, decimal_places=4)
    allow_tracking = models.BooleanField()
    is_wiped = models.BooleanField()
    wipe_requested = models.BooleanField()

    def __unicode__(self):
	return self.name
