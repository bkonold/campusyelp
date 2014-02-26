from django.db import models

# Create your models here.


class Food(models.Model):
	title = models.CharField(max_length=100, unique=True)

	def __unicode__(self):
		return "Food: " + self.title
class Menu(models.Model):
	day = models.DateField(primary_key=True)
	food_items = models.ManyToManyField(Food)

	def __unicode__(self):
		return "Food Items: [" + food_items.join(" ") + "]"

class Review(models.Model):
	title = models.CharField(max_length=100)
	content = models.CharField(max_length=500)
	rating = models.IntegerField()
	food_item = models.ForeignKey(Food)

	def __unicode__(self):
		return "Review: " + self.title

class Image(models.Model):
	path = models.CharField(max_length=200)
	food_item = models.ForeignKey(Food)

	def __unicode__(self):
		return "Path: " + self.path + "\n" + "Food: " + self.food_item