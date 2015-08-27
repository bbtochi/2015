__author__ = ''


from tweepy import Stream
from tweepy import OAuthHandler
from tweepy.streaming import StreamListener
from tweepy import API
import time
import datetime
from key import ap_id, rest_key, key, secret, token, secret2, database
from alchemyapi import AlchemyAPI
import ibm_db


conn = ibm_db.connect(database, "gqxbnfjt", "byrkrr79aie9")


def grouper(tweet):
        response = alchemyapi.sentiment("text", tweet.split(":: ")[1].lower())
        if response['docSentiment']['type'] == 'positive':
            if float(response['docSentiment']['score']) > 0.31:
                return 1
        elif response['docSentiment']['type'] == 'neutral':
            return 2
        elif float(response['docSentiment']['score'])  >= -0.3 and float(response['docSentiment']['score']) <= 0.3:
            return 2
        elif response['docSentiment']['type'] == "negative":
            if float(response['docSentiment']['score']) <= -0.31 and float(response['docSentiment']['score']) >= -0.5:
                return 3
            elif float(response['docSentiment']['score']) <= -0.51 and float(response['docSentiment']['score']) >= -1:
                return 4


def screen_name(data):
    api = API(oauth)
    user = api.get_user(data.split(',"screen_name":"')[1].split('","location')[0])
    return user.screen_name


def handler(data):
    tweet = data.split(',"text":"')[1].split('","source')[0]
    if tweet.startswith("["):
        if "&gt;" in str(tweet):
            tweet = data.split(',"text":"')[1].split('","source')[0].split('"')[1]
            tweet = datetime.datetime.fromtimestamp(int(time.time())).strftime('%H:%M %m-%d-%Y ') + ' :: '\
                    + tweet.replace("&gt;", ">")
            #parser(tweet, data)
            db_tweet = DbTweet(tweet, data)
            db_tweet.save()
    elif tweet.startswith("RT"):
        pass # ignore tweet
    else:
        pass # ignore tweet


class DbTweet:

    def __init__(self, tweet, data):
        self.time = tweet.split(" :: ")[0].split()[0]
        self.date = tweet.split(" :: ")[0].split()[1]
        self.tweet = tweet.split(" :: ")[1]
        self.user = "@" + screen_name(data)
        self.congestion = grouper(tweet)


    def save(self):
        if self.congestion != None:
            sql_insert = ("INSERT INTO GQXBNFJT.TWEET_TEMP (USER,TWEET,DATE,TIME, CONGESTION) "
                        "VALUES ('%s', '%s', '%s', '%s', '%s')")%(self.user, self.tweet, self.date, self.time, self.congestion)
            stmt = ibm_db.exec_immediate(conn, sql_insert)


class Getter(StreamListener):

    def on_data(self, data):
        handles = ["TotalTrafficNYC", "Z100Traffic", "EWNTraffic", "CapeTownFreeway","TRAFFICBUTTERAPP"]
        try:
            if str(screen_name(data)) == "Gidi_Traffic":
                handler(data)
            elif str(screen_name(data)) in handles:
                tweet = data.split(',"text":"')[1].split('","source')[0]
                tweet = datetime.datetime.fromtimestamp(int(time.time())).strftime('%H:%M %m-%d-%Y ') + ' :: ' + tweet
                db_tweet = DbTweet(tweet, data)
                db_tweet.save()
                print tweet
            else:
                pass #ignore mentions
        except BaseException, e:
            print str(e)
            time.sleep(5)

    def on_error(self, status):
        print status


if __name__ == '__main__':

    alchemyapi = AlchemyAPI()
    oauth = OAuthHandler(key, secret)
    oauth.set_access_token(token, secret2)
    twitterStream = Stream(oauth, Getter())
    follow_list = ['42640432', '42744320', '378809160', '72218172', '159074771', '216331785']
    twitterStream.filter(follow=follow_list)
