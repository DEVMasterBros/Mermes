from kafka import KafkaProducer
from kafka.errors import KafkaError
import msgpack
import oauth2 as oauth
import json
def getTimeLine():

  consumer_key = "f13sxP3rWdUBmgzYG4D6ckNwO"
  consumer_secret = "ZWtOdfJy9lVEbfbX7I2UJqfsnHW9ZYThEcVbwCeEOutxuQECJq"
  access_token = "1020172740098080768-rkXQXn9sJhIhN98HP6EZOjuacydV0H"
  access_token_secret = "hRi5SiZrn9Id0CfxWflPEgQjMbeZKNcDl1nufAj3mk5aM"

  consumer = oauth.Consumer(key=consumer_key, secret=consumer_secret)
  access = oauth.Token(key=access_token, secret=access_token_secret)

  client = oauth.Client(consumer, access)

  home_time_line = "https://api.twitter.com/1.1/statuses/home_timeline.json?count=199"
  response, data = client.request(home_time_line)
  str_data = data.decode('utf-8')
  return str_data

def kafka_produce():
    tmp_message=getTimeLine()
    message=json.loads(tmp_message)
    result=""
    for str in message:
        result+=str['text']

    producer = KafkaProducer(bootstrap_servers=['localhost:9092'])
    producer = KafkaProducer(value_serializer=lambda m:m.encode('utf-8'))
    #producer.send('msgpack-topic', message)
    #producer = KafkaProducer(value_serializer=msgpack.dumps)
    producer.send("baro4", result)
    producer.close();

if __name__=='__main__':
    kafka_produce()
    #consumer.getDataFromKafka()
