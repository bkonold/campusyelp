from django.db import models

# Create your models here.

class Menu(models.Model):
	day = models.DateField(primary_key=True)
	food_items = models.ManyToManyField(Food)

class Food(models.Model):
	title = models.CharField(max_length=100, unique=True)

class Review(models.Model):
	title = models.CharField(max_length=100)
	content = models.CharField(max_length=500)
	rating = models.IntField()
	food_item = models.ForeignKey(Food)

class Image(models.Model):
	path = models.CharField(max_length=200)
	food_item = models.ForeignKey(Food)