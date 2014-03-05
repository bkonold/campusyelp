from models import *

class ViewHelper:

    @staticmethod
    def addOrFetch(foods):
        for i in range(0, len(foods)):
            food = foods[i]
            exists = Food.objects.filter(title=food).count() != 0
            if not exists:
                f = Food(title=food)
                f.save()
                foods[i] = {"title": f.title, "id": f.id, "rating": 0}
            else:
                f = Food.objects.filter(title=food)[0]
                foods[i] = {"title": f.title, "id": f.id, "rating": f.rating}
