from bs4 import BeautifulSoup
import urllib2
import re


class MenuScraper:

	def __init__(self):
		# get html
		try: 
			html = urllib2.urlopen("http://menu.ha.ucla.edu/foodpro/default.asp").read()
		except urllib2.URLError:
			try:
				f = open("menuHTML.html", 'r')
				html = f.read()
			except:
				print "no connection or local HTML menu"
				exit()

		# convert to beautitfulsoup object
		soup = BeautifulSoup(html)

		# extract text from html
		text = soup.get_text()

		# remove extraneous characters(tabs, weird spaces, extra new lines, etc)
		notabs = re.sub("\t+" , "\n", text)
		space = re.sub("\xa0" , " ", notabs)
		withSymbol = re.sub("\nw/|w/\n", "w/", space)
		extraneous = re.sub("(Full Menu.*\n)|(Click.*\n)" , "\n", withSymbol)
		emptyLines = re.sub("\n+ +| +\n", "\n", extraneous)
		final = re.sub("[\n\r]{2,}", "\n\n", emptyLines)


		lunchStart = final.find("Lunch Menu")
		dinnerStart = final.find("Dinner Menu")
		dinnerEnd = final.find("Back to Top")

		# get each part
		lunchText = final[lunchStart:dinnerStart].strip()
		dinnerText = final[dinnerStart:dinnerEnd].strip()

		#create list with menuData
		dinnerList = [i.split("\n") for i in dinnerText.encode('ascii', 'ignore').split("\n\n")]
		lunchList = [i.split("\n") for i in lunchText.encode('ascii', 'ignore').split("\n\n")]

		diningHalls = ["Covel", "De Neve", "FEAST at Rieber", "Bruin Plate"]

		# get positions of dining hall list
		lunchPos = []
		for it, i in enumerate(lunchList):
			for name in i:
				if (name in diningHalls):
					lunchPos.append(it)
					break

		lunchSet1 = lunchList[lunchPos[0]:lunchPos[1]]
		lunchSet2 = lunchList[lunchPos[1]:]

		self.covelLunch = [i for it, i in enumerate(lunchSet1[1:]) if it%2 == 0]
		self.deneveLunch = [i for it, i in enumerate(lunchSet1[1:]) if it%2 == 1]
		self.feastLunch = [i for it, i in enumerate(lunchSet2[1:]) if it%2 == 0]
		self.bpLunch = [i for it, i in enumerate(lunchSet2[1:]) if it%2 == 1]

		dinnerPos = []
		for it, i in enumerate(dinnerList):
			for name in i:
				if (name in diningHalls):
					dinnerPos.append(it)
					break

		dinnerSet1 = dinnerList[dinnerPos[0]:dinnerPos[1]]
		dinnerSet2 = dinnerList[dinnerPos[1]:]

		self.covelDinner = [i for it, i in enumerate(dinnerSet1[1:]) if it%2 == 0]
		self.deneveDinner = [i for it, i in enumerate(dinnerSet1[1:]) if it%2 == 1]
		self.feastDinner = [i for it, i in enumerate(dinnerSet2[1:]) if it%2 == 0]
		self.bpDinner = [i for it, i in enumerate(dinnerSet2[1:]) if it%2 == 1]

	def covelLunch():
		return self.covelLunch

	def deneveLunch():
		return self.deneveLunch

	def feastLunch():
		return self.feastLunch

	def bpLunch():
		return self.bpLunch

	def covelDinner():
		return self.covelDinner

	def deneveDinner():
		return self.deneveDinner

	def feastDinner():
		return self.feastDinner

	def bpDinner():
		return self.bpDinner
