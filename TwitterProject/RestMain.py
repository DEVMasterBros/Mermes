import oauth2 as oauth
import json
from pyspark import SparkContext


def init():

  consumer_key = "2fk2BAgAYqCmh2rhPNcIu4nsR"
  consumer_secret = "H3vfoh1QG9ZtS5bGzy2kxArf9mEGd1J6iNvOSNTUxMbPwsdr6H"
  access_token = "1020172740098080768-jHSSxz9oHKQv7it01vU472B7OtiL1O"
  access_token_secret = "2TCQMpXhlC5uHGgcgW4YvqpQEuAwyYwOt8kTkzHp2QEpV"

  consumer = oauth.Consumer(key=consumer_key, secret=consumer_secret)
  access = oauth.Token(key=access_token, secret=access_token_secret)

  client = oauth.Client(consumer, access)

  home_time_line = "https://api.twitter.com/1.1/statuses/home_timeline.json?count=199"
  response, data = client.request(home_time_line)
  str_data = data.decode('utf-8')
  tweets = json.loads(str_data)

  with open('HomeTime.txt', 'w', encoding='utf-8') as file_object:
    for tweet in tweets:
      file_object.write(tweet['text'] + '\n')
    file_object.close()


def dataProcess():

   sc=SparkContext('local')
   text_file=sc.textFile("/home/kimbalo/PycharmProjects/TwitterTest/TwitterProject/HomeTime.txt")
   count=text_file.flatMap(lambda line:line.split(" "))\
   .flatMap(lambda line:line.split("-"))\
   .flatMap(lambda line:line.split("#"))\
   .flatMap(lambda line:line.split("["))\
   .flatMap(lambda line:line.split("]"))\
   .flatMap(lambda line:line.split("&"))\
   .filter(lambda line: line!="") \
   .map(lambda word:(word,1))\
   .reduceByKey(lambda a,b:a+b)\
   .map(lambda t:(t[1],t[0]))\
   .sortByKey(ascending=False)\


   count.saveAsTextFile("/home/kimbalo/PycharmProjects/TwitterTest/TwitterProject/result")

if __name__=='__main__':
   #init()
   dataProcess()