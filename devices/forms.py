from django import forms

class LocationForm(forms.Form):
    track_frequency = forms.CharField(max_length=12)
