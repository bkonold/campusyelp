from django.shortcuts import render
from menu_scraper import MenuScraper
from django.http import *
from django.views.decorators.csrf import csrf_exempt
from models import *
from model_helper import JsonHelper

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

@csrf_exempt
def reviews(request, food_id):
    if request.method == "GET":
        reviews = Review.objects.filter(food_id=food_id);
        dict_list = [JsonHelper.buildReviewDict(r) for r in reviews]
        json_obj = {"reviews": dict_list}
        return HttpResponse(json.dumps(json_obj), content_type="application/json")
    elif request.method == "POST": 
        try:
            r = JsonHelper.buildReviewFromJson(request.body)
            try:
                r.save()
                return HttpResponse(status=201)
            except:
                return HttpResponseServerError("Could not save review to database")
        except:
            return HttpResponseBadRequest("Invalid POST data!")
    else:
        return HttpResponseBadRequest("This endpoint only supports get and post!") 


@csrf_exempt
def images(request, food_id):
    if request.method == "GET":
        #do shit
        return HttpResponse("Implement Me!")
    elif request.method == "POST":
        #post some shit
        return HttpResponse("Implement Me!")
    else:
        return HttpResponseBadRequest("This endpoint only supports get and post!") 





