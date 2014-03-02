from django.db import models

# Create your models here.

class Food(models.Model):
	title = models.CharField(max_length=100, unique=True)

	def __unicode__(self):
		return self.title

class Review(models.Model):
	title = models.CharField(max_length=100)
	content = models.CharField(max_length=500)
	rating = models.FloatField()
	food = models.ForeignKey(Food)

	def __unicode__(self):
		return self.title