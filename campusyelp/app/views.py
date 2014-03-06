from django.shortcuts import render
from menu_scraper import MenuScraper
from django.http import *
from django.views.decorators.csrf import csrf_exempt
from models import *
import model_helper
import view_helper

import os
import base64
import json
# Create your views here.
# request: the request object
def menu(request):
    scraped_menus = MenuScraper()
    menus = {"covel": {"lunch": [], "dinner": []},
             "deneve": {"lunch": [], "dinner": []},
             "bp": {"lunch": [], "dinner": []},
             "feast": {"lunch": [], "dinner": []}}

    for l in scraped_menus.covelLunch:
        foodList = l[1:]
        view_helper.addOrFetch(foodList)
        menus["covel"]["lunch"].append({"station": l[0], "items": foodList})

    for l in scraped_menus.deneveLunch:
        foodList = l[1:]
        view_helper.addOrFetch(foodList)
        menus["deneve"]["lunch"].append({"station": l[0], "items": foodList})

    for l in scraped_menus.bpLunch:
        foodList = l[1:]
        view_helper.addOrFetch(foodList)
        menus["bp"]["lunch"].append({"station": l[0], "items": foodList})

    for l in scraped_menus.feastLunch:
        foodList = l[1:]
        view_helper.addOrFetch(foodList)
        menus["feast"]["lunch"].append({"station": l[0], "items": foodList})

    for l in scraped_menus.covelDinner:
        foodList = l[1:]
        view_helper.addOrFetch(foodList)
        menus["covel"]["dinner"].append({"station": l[0], "items": foodList})

    for l in scraped_menus.deneveDinner:
        foodList = l[1:]
        view_helper.addOrFetch(foodList)
        menus["deneve"]["dinner"].append({"station": l[0], "items": foodList})

    for l in scraped_menus.bpDinner:
        foodList = l[1:]
        view_helper.addOrFetch(foodList)
        menus["bp"]["dinner"].append({"station": l[0], "items": foodList})

    for l in scraped_menus.feastDinner:
        foodList = l[1:]
        view_helper.addOrFetch(foodList)
        menus["feast"]["dinner"].append({"station": l[0], "items": foodList})


    return HttpResponse(json.dumps(menus), content_type="application/json")

@csrf_exempt
def reviews(request, food_id):
    if request.method == "GET":
        foods = Food.objects.filter(id=food_id)
        if (len(foods) < 1):
            return HttpResponseNotFound();
        food = foods[0]
        reviews = Review.objects.filter(food_id=food_id)
        dict_list = [model_helper.buildReviewDict(r) for r in reviews]
        json_obj = {"rating": food.rating, "reviews": dict_list}
        return HttpResponse(json.dumps(json_obj), content_type="application/json")
    elif request.method == "POST": 
        try:
            foods = Food.objects.filter(id=food_id)
            if (len(foods) < 1):
                return HttpResponseNotFound();
            food = foods[0]
            r = model_helper.buildReviewFromJson(request.body, food_id)
            try:
                r.save()
                oldtotal = food.rating * float(food.numreviews)
                food.numreviews += 1
                food.rating = (oldtotal + r.rating) / food.numreviews
                food.save()
                return HttpResponse(status=201)
            except:
                return HttpResponseServerError("Could not save review to database")
        except:
            return HttpResponseBadRequest("Invalid POST data!")
    else:
        return HttpResponseBadRequest("This endpoint only supports GET and POST!") 


@csrf_exempt
def images(request, food_id):
    img_base_path = os.path.join(os.path.abspath("."), "images/%s" % food_id)
    #return highest id of all existing images for food_id, or return 404 Not Found if none
    if request.method == "GET":

        if not os.path.isdir(img_base_path):
            return HttpResponseNotFound("No images")

        max_id = view_helper.getMaxId(img_base_path)

        if max_id == 0:
            return HttpResponseNotFound("No images")
        else:
            return HttpResponse('{ "max_id": %d}' % max_id, content_type="application/json")

    #save the image encoded in the post request to the proper location
    elif request.method == "POST":
        try:
            d = json.loads(request.body)
            try:
                if os.path.isdir(img_base_path):
                    img_id = view_helper.getMaxId(img_base_path)+1
                else:
                    img_id = 1
                img_file = open("%s/%d.jpg" % (img_base_path,img_id), "w")
                img_file.write(base64.decodestring(d["base64"]))
                img_file.close()
            except:
                return HttpResponseServerError("Unable to save image :(")
        except: 
            return HttpResponseBadRequest("Invalid POST data!")
    else:
        return HttpResponseBadRequest("This endpoint only supports GET and POST!") 

@csrf_exempt
def image(request, food_id, img_id):
    if request.method == "GET":
        img_base_path = os.path.join(os.path.abspath("."), "images/%s" % food_id)
        file_name = "%s.jpg" % img_id
        file_path = "/".join([img_base_path, file_name])
        try:
            try:
                f = open(file_path, 'r')
                img64 = base64.b64encode(f.read())
                f.close()
		d = {"base64": img64}
                return HttpResponse(json.dumps(d), content_type="application/json")
            except IOError:
                return HttpResponseNotFound("Image doesn't exist")
        except:
            return HttpResponseNotFound("Image does not exist: <br>" + file_path)
    else: 
        return HttpResponseBadRequest("Only GET is supported by this endpoint!")





