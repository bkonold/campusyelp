from django.shortcuts import render
from menu_scraper import MenuScraper
from django.http import *
from django.views.decorators.csrf import csrf_exempt
from models import *
from model_helper import JsonHelper
from view_helper import ViewHelper

import os
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
        foodList = l[1:]
        ViewHelper.addOrFetch(foodList)
        menus["covel"]["lunch"] = {"station": l[0], "items": foodList}

    for l in scraped_menus.deneveLunch:
        foodList = l[1:]
        ViewHelper.addOrFetch(foodList)
        menus["deneve"]["lunch"] = {"station": l[0], "items": foodList}

    for l in scraped_menus.bpLunch:
        foodList = l[1:]
        ViewHelper.addOrFetch(foodList)
        menus["bp"]["lunch"] = {"station": l[0], "items": foodList}

    for l in scraped_menus.feastLunch:
        foodList = l[1:]
        ViewHelper.addOrFetch(foodList)
        menus["feast"]["lunch"] = {"station": l[0], "items": foodList}

    for l in scraped_menus.covelDinner:
        foodList = l[1:]
        ViewHelper.addOrFetch(foodList)
        menus["covel"]["dinner"] = {"station": l[0], "items": foodList}

    for l in scraped_menus.deneveDinner:
        foodList = l[1:]
        ViewHelper.addOrFetch(foodList)
        menus["deneve"]["dinner"] = {"station": l[0], "items": foodList}

    for l in scraped_menus.bpDinner:
        foodList = l[1:]
        ViewHelper.addOrFetch(foodList)
        menus["bp"]["dinner"] = {"station": l[0], "items": foodList}

    for l in scraped_menus.feastDinner:
        foodList = l[1:]
        ViewHelper.addOrFetch(foodList)
        menus["feast"]["dinner"] = {"station": l[0], "items": foodList}


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
            r = JsonHelper.buildReviewFromJson(request.body, food_id)
            try:
                r.save()
                return HttpResponse(status=201)
            except:
                return HttpResponseServerError("Could not save review to database")
        except:
            return HttpResponseBadRequest("Invalid POST data!")
    else:
        return HttpResponseBadRequest("This endpoint only supports GET and POST!") 


@csrf_exempt
def images(request, food_id):
    #return highest id of all existing images for food_id, or return 404 Not Found if none
    if request.method == "GET":
        img_base_path = os.path.join(os.path.abspath("."), "images/%s" % food_id)

        try:
            img_dir = open(img_base_path, 'r')
        except:
            return HttpResponseNotFound("No images")

        max_id = 0

        for filename in os.listdir(img_base_path):
            if filename.endswith(".jpg"):
                i_id = int(os.path.splitext(filename)[0])
                max_id = i_id if i_id > max_id else max_id

        if max_id == 0:
            return HttpResponseNotFound("No images")
        else:
            return HttpResponse('{ "max_id": %d}' % max_id, content_type="application/json")

    #save the image encoded in the post request to the proper location
    elif request.method == "POST":
        return HttpResponse("Implement Me!")
    else:
        return HttpResponseBadRequest("This endpoint only supports get and post!") 

@csrf_exempt
def image(request, food_id, img_id):
    if request.method == "GET":
        img_base_path = os.path.join(os.path.abspath("."), "images/%s" % food_id)
        file_name = "%s.jpg" % img_id
        file_path = "/".join([img_base_path, file_name])
        try:
            img_data = open(file_path, "r").read()
            return HttpResponse(img_data, mimetype="image/jpg")
        except:
            return HttpResponseNotFound("Image does not exist: <br>" + file_path)
    else: 
        return HttpResponseBadRequest("Only GET is supported by this endpoint!")





