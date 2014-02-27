from django.shortcuts import render
from menu_scraper import MenuScraper
from django.http import *

import json
# Create your views here.
# request: the request object
def menu(request):
	scraped_menus = MenuScraper()
	menus = {"covel": {"lunch": {}, "dinner": {}},
			 "deneve": {"lunch": {}, "dinner": {}},
			 "bp": {"lunch": {}, "dinner": {}},
			 "feast": {"lunch": {}, "dinner": {}}}
	for l in scraped_menus.covelLunch:
		menus["covel"]["lunch"][l[0]] = l[1:]
	for l in scraped_menus.deneveLunch:
		menus["deneve"]["lunch"][l[0]] = l[1:]
	for l in scraped_menus.bpLunch:
		menus["bp"]["lunch"][l[0]] = l[1:]
	for l in scraped_menus.feastLunch:
		menus["feast"]["lunch"][l[0]] = l[1:]

	for l in scraped_menus.covelDinner:
		menus["covel"]["dinner"][l[0]] = l[1:]
	for l in scraped_menus.deneveDinner:
		menus["deneve"]["dinner"][l[0]] = l[1:]
	for l in scraped_menus.bpDinner:
		menus["bp"]["dinner"][l[0]] = l[1:]
	for l in scraped_menus.feastDinner:
		menus["feast"]["dinner"][l[0]] = l[1:]

	return HttpResponse(json.dumps(menus), content_type="application/json")






