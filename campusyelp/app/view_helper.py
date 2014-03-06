from models import *
import os
def addOrFetch(foods):
    for i in range(0, len(foods)):
        food = foods[i]
        exists = Food.objects.filter(title=food).count() != 0
        if not exists:
            f = Food(title=food, numreviews=0.0, rating=0.0)
            f.save()
            foods[i] = {"title": f.title, "id": f.id, "rating": f.rating}
        else:
            f = Food.objects.filter(title=food)[0]
            foods[i] = {"title": f.title, "id": f.id, "rating": f.rating}

def getMaxId(img_dir):
    max_id = 0
    for filename in os.listdir(img_base_path):
        if filename.endswith(".jpg"):
            i_id = int(os.path.splitext(filename)[0])
            max_id = i_id if i_id > max_id else max_id
    return max_id
