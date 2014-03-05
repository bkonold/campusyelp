from models import *

import json

class JsonHelper:
       
    @staticmethod
    def buildReviewDict(review):
        return {"content": review.content,
                "rating": review.rating}

    @staticmethod
    def buildReviewFromJson(review_json, food_id):
        d = json.loads(review_json)
        return Review(content=d["content"], rating=d["rating"], food_id=food_id)