from django.conf.urls import patterns, url

from app import views

urlpatterns = patterns('', 
	url(r'^menu$', views.menu, name='menu')
)