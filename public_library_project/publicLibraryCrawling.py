from bs4 import BeautifulSoup
import requests
import time
import json
import pymongo
import re
def main():
    session = requests.session()

# 한글 철자 모음 리스트
    with open("korean.txt","r",encoding="utf-8") as f:
        char = f.read()
        list_kor = []
        for i in char:
            list_kor.append(i)

    list_kor = list_kor[1:len(list_kor)]

    #영어 철자 모음 리스트
    list_eng_s = ["a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"]

    list_eng_l = []
    for c in list_eng_s:
        list_eng_l.append(c.upper())

    #숫자 모음 리스트
    list_dig = ["0","1","2","3","4","5","6","7","8","9"]
    #--------------------------------------------------------------------------------------------------------------------
    lists = list_kor + list_eng_s + list_eng_l + list_dig

    API_KEY = "a4e3f95d68d41e52302efe7c6fbd2abf"
    category = ["dan", "yon", "media", "disabled", "web", "map", "music", "etc", "archive", "cip", "korcis"]
    mainurl = 'http://www.nl.go.kr/app/nl/search/openApi/search.jsp?key='

    urls = scrape_list_page(lists, category, mainurl, API_KEY) #123574개

    library_book_list = []
    for url in urls:
        time.sleep(1)
        response = session.get(url)
        bookInfo = scrape_detail_page(response)
        #print(bookInfo)
        library_book_list.append(bookInfo)
        break

    library_book_list_new = list_clear(library_book_list)

    categorized_book_list = categorization(library_book_list_new)
    #print(categorized_book_list[1])
   
    with open("publicLibrary10.json", "w", encoding="utf-8-sig") as f:
        json.dump(categorized_book_list, fp=f, ensure_ascii=False, indent=3)

    insertMongDB(categorized_book_list)


def scrape_list_page(lists, category,mainurl,API_KEY):
    for x in lists:
        for y in category:
            #url =  list_url.append(url + API_KEY + '&kwd=' + x + '&pageSize=500&pageNum={}'.format+ '&category=' + y)
            url = "{}{}&kwd={}&pageSize=500&pageNum=1&category={}&topF1=title".format(mainurl,API_KEY,x,y)
            yield url

def scrape_detail_page(response):
    try:
        soup = BeautifulSoup(response.text,'html.parser') 
        bookInfo = []
        #count = 1
        for i in range(len(soup.select("title_info"))):
            category = soup.select("category")[0].string
            title = re.sub(r'</?b>','',soup.select("title_info")[i].string)
            type = soup.select("type_name")[i].string
            author =soup.select("author_info")[i].string
            place = soup.select("place_info")[i].string
            no = soup.select("call_no")[i].string
            id = soup.select("id")[i].string
            classification = soup.select("kdc_name_1s")[i].string
            #count += 1

            dict = {
                'category' : category,
                'title' : title,
                'type' : type,
                'author' : author,
                'place' : place,
                'no' : no,
                'id' : id,
                'classification' : classification
                #'count' : count
             }
            bookInfo.append(dict)
        return bookInfo
    except Exception as e:
        print("페이지 파싱 에러", e)


def list_clear(list):
    id_list = []
    lists= []
    for x in (list):
        for y in x:
            if y['id'] and y['id'] not in id_list:
                id_list.append(y['id'])
                lists.append(y)
            else:
                pass
    return lists

def categorization(list):
    socialScience = [] #사회과학
    naturalScience = [] #자연과학
    religion = [] #종교
    philosophy = [] #철학
    technoScience = [] #기술과학
    literature = [] # 문학
    language = [] #어학
    history = []#역사
    art = []#예술
    etc = [] #기타
    science = [] #순수과학
    total = [] # 총류

    for n in list:
    #print(n)
        if n['classification'] == '사회과학':
            socialScience.append(n)
        elif n['classification'] == '자연과학':
            naturalScience.append(n)
        elif n['classification'] == '종교':
            religion.append(n)
        elif n['classification'] == '철학':
            philosophy.append(n)
        elif n['classification'] == '기술과학':
            technoScience.append(n)
        elif n['classification'] == '문학':
            literature.append(n)
        elif n['classification'] == '어학':
            language.append(n)
        elif n['classification'] == '역사':
            history.append(n)
        elif n['classification'] == '예술':
            art.append(n)
        elif n['classification'] == '기타':
            etc.append(n)
        elif n['classification'] == '순수과학':
            science.append(n)
        else:
            total.append(n)

    totlal_list = [socialScience, naturalScience, religion, philosophy, technoScience, literature, language, history,art, etc, science, total]    
    return totlal_list

def insertMongDB(list):
    myclient = pymongo.MongoClient('mongodb://localhost:27017/')
    #database 생성
    mydb = myclient['libraryBooks10']  #libraryBooks
    #print(myclient.list_database_names())

    '''
    #데이터베이스 존재 여부 확인
    dblist = myclient.list_database_names()
    
    if 'libraryBooks' in dblist:
        print("The database exists.")
    else:
        print("The database does not exist")
    '''
    #collection 생성
    kdc_list = ["socialScience", "naturalScience", "religion", "philosophy", "technoScience", "literature", 
    "language", "history", "art", "etc", "science", "total"]

    i = 0
    for a in kdc_list:
        a = mydb[a] 
        a.insert_many(list[i])
        i += 1      
        #print("********************************", a ,"************************************")
        for b in a.find():
            print(b)
        
if __name__ == "__main__":
    main()