from models import *

import json

class JsonHelper:
    @staticmethod
    def buildFoodDict(food):
        return {"title": food.title}
        
    @staticmethod
    def buildReviewDict(review):
        return {"title": review.title,
                "content": review.content,
                "rating": review.rating,
                "food_id": review.food_id}

    @staticmethod
    def buildReviewFromJson(review_json):
        d = json.loads(review_json)
        return Review(title=d["title"], content=d["content"], rating=d["rating"], food_id=d["food_id"])