CREATE TABLE Menu(day date,
				  foodId int
				  FOREIGN KEY (foodId) REFERENCES Food(id)
				  PRIMARY KEY(day, foodId))
				  ENGINE=INNODB;

CREATE TABLE Food (id int,
				   title varchar(100), 
				   PRIMARY KEY(id),
				   UNIQUE(title)) 
				   ENGINE=INNODB;

CREATE TABLE FoodImage(foodId int,
					   imagePath varchar(100)
					   PRIMARY KEY(foodId, imagePath))
					   ENGINE=INNODB;

CREATE TABLE Review (id int, 
					title varchar(100),
					content varchar(500),
					rating int,
					PRIMARY KEY(id),
					FOREIGN KEY (title) references Food(title))
					ENGINE=INNODB;

CREATE TABLE FoodReview (foodId int,
						reviewId int,
						FOREIGN KEY (foodId) references Food(id),
						FOREIGN KEY (reviewId) references Review(id))
						ENGINE=INNODB;