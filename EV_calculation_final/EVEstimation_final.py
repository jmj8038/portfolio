from selenium import webdriver
from bs4 import BeautifulSoup as bs
import pandas as pd
import time
from urllib.request import urlopen
import numpy as np
import pymongo

def main():
    # 기업의 재무정보 
    ipo = input("상장된 회사입니까? y or n : ")
    if ipo == "y" or  ipo == "yes" or ipo == "Y":
        comp = input("기업 가치를 알고 싶은 회사: ")
        FS = getFinancialStatementIPO(comp)
        BS = FS[0]
        IS = FS[1]
        CF = FS[2]
        BW = FS[3]
        NO = FS[4]
    else:
        print("현재 개발 중입니다.")
        #comp = input("기업 가치를 알고 싶은 회사: ")
        #FS = getFinancialStatementNoIPO(comp)
        #BS = FS[0]
        #IS = FS[1]
        #CF = FS[2]
        #BW = FS[3]
        #NO = FS[4] 

    # 산업 성장률 계산
    #I_Growth = IndustryGrowth()
    I_Growth = 0.02 # 현대증권 경제 보고서 참조

    # WACC 계산 STEP1 - beta 계산
    betaCal = BetaCal(comp)
    bMatric = betaCal[0]
    bBeta = betaCal[1]
    #print(bMatric)
    #print(bBeta)
    
    # WACC 계산 STEP2 - 주식 수익률 계산
    Retruns = ReturnCal(bMatric, bBeta)
    Rf = Retruns[0]
    Rm = Retruns[1]
    Re = Retruns[2]
    #print(Rf,"-----",Rm,"------",Re)

    # 과거 5년 FCFF 및 FCFF 성장률 계산
    H_FCFF = HFCFF_CAL(BS, IS, CF, BW)
    FCFF = H_FCFF[0]
    FCFF_Grate = H_FCFF[1]
    #print(FCFF_Grate)

     # 미래 10년 및 Terminal Value 계산
    #F_FCFF = FFCFF_CAL(FCFF,FCFF_Grate,I_Growth)
    F_FCFF = FFCFF_CAL(FCFF,FCFF_Grate)
    #print(type(F_FCFF))

    EV = EV_Calculation(BS, CF, BW, NO, FCFF, Re, F_FCFF, I_Growth)
    #iRate, WACC, EnterpriseValue, EquityValue, StockPrice
    iRate = EV[0]
    WACC = EV[1]
    EnterpriseValue = EV[2]
    EquityValue = EV[3]
    StockPrice = EV[4]
    
    # 매수 및 매도 의견
    OpinionD = OpinionDecision(bMatric, StockPrice)
    todayP = OpinionD[0]
    opinion = OpinionD[1]

    # 결과값 출력
    PRINT(comp, Rf, Rm, Re, iRate, WACC, EnterpriseValue, EquityValue, StockPrice, todayP, opinion)

    # MongoDB 입력
    InsertMongDB(comp, Rf, Rm, Re, iRate, WACC, EnterpriseValue, EquityValue, StockPrice, todayP, opinion)
#-----------------------------------------------------------------------------------------------

def getFinancialStatementIPO(comp):
    driver = webdriver.Chrome('C:\driver\chromedriver.exe')
    driver.get("http://media.kisline.com/search/mainSearch.nice")

    tag = driver.find_element_by_name("name")
    tag.clear()
    tag.send_keys(comp)

    xpath1 = """//*[@id="search"]"""
    item_click = driver.find_element_by_xpath(xpath1).click()

    time.sleep(1)

    xpath2 = """//*[@id="container"]/ul/li[1]/a"""
    firm_click = driver.find_element_by_xpath(xpath2).click()

    time.sleep(1)

    xpath3 = """//*[@id="nav"]/li[4]/div/a/img"""
    finannce_inf = driver.find_element_by_xpath(xpath3).click()

    time.sleep(1)

    html = driver.page_source

    tables = pd.read_html(html)
    BS=tables[4] # == 재무제표
    IS=tables[12]# == 포괄손익계산서
    CF=tables[20]# == 현금흐름표
    
    # 발행주식수 crawling
    xpath4 = """//*[@id="nav"]/li[3]/div/a/img"""
    investment_inf = driver.find_element_by_xpath(xpath4).click()

    html = driver.page_source
    soup = bs(html,'html.parser')
    num = soup.select('tr > td ')[4].string
    list = num.replace(" ","").replace(",","").split("/")
    NO = (int(list[0]) + int(list[1]))*1000

    #차입금
    borrowing=tables[0].iloc[36].values.tolist()[1:]
    borrowing[-1]=tables[0].iloc[36].values.tolist()[-2]
    borrowing1=np.array(borrowing, dtype=float)*100000000
    BW=borrowing1.tolist()
    
    driver.close()
    
    return BS, IS, CF, BW, NO

def HFCFF_CAL(BS, IS, CF, BW):
    CFO=CF.iloc[0,[1,2,3,4,5]].astype(float)*1000000 #영업활동으로 인한 현금 흐름
    INT=CF.iloc[5,[1,2,3,4,5]].astype(float)*1000000 #이자비용 지출(금융비용)
    TAXRATE=IS.iloc[12,[1,2,3,4,5]].astype(float)/IS.iloc[11,[1,2,3,4,5]].astype(float) #TAX RATE == 법인세비용/법인세비용차감전순이익(포괄손익계산서)
    CAPEX=CF.iloc[9,[1,2,3,4,5]].astype(float)*1000000 #CAPEX
    a=[CFO,INT,TAXRATE,CAPEX]
    b=pd.DataFrame(a) #, index=(["CFO","INT","TAXRATE","CAPEX"]))
    c=b.iloc[0]+b.iloc[1]*(1-b.iloc[2])+b.iloc[3] #FCFF계산
    FCFF=b.append([c])
    #FCFF.index = ["CFO","INT","TAXRATE","CAPEX", "FCFF"]
    FCFF["2018.12.31"] = FCFF.iloc[:,[4]]*(4/3)
    FCFF.astype(int)
    #FCFF=FCFF.round(2)
    del FCFF['2018.09.30']
    FCFF=FCFF.append(FCFF.iloc[4].pct_change()) #FCFF 성장률 계산
    FCFF.index = ["CFO","INT","TAXRATE","CAPEX", "FCFF","FCFF_growthRate"]
    #FCFF_Grate = FCFF.iloc[5].mean()
    FCFF_Grate = FCFF.iloc[5,[3,4]].mean()                  #성장률 조정
    return FCFF, FCFF_Grate

'''
def IndustryGrowth():
    #g(성장률)
    GrowthRate=pd.read_excel('연간지표_20181218215412.xlsx')
    GrowthRate=(GrowthRate.iloc[17][1]+GrowthRate.iloc[17][2]+GrowthRate.iloc[17][3])/3
    GrowthRate

    G=GrowthRate/100-0.01 #성장률 (조정변수(0.01))
    G
'''

'''
def FFCFF_CAL(FCFF,FCFF_Grate,I_Growth):
    FCFF_G = FCFF_Grate
    DEPRECIATION=(FCFF_G-I_Growth)/10
    FUTURE=['2019','2020','2021','2022','2023','2024','2025','2026','2027','2028']
    # 미래 10년 FCFF 성장률
    rst=[]
    for i in range(10):
        rst.append(FCFF_G)
        FCFF_G=FCFF_G-DEPRECIATION

    rst1=[{FUTURE[i]:rst[i] for i in range(10)}]
    df=pd.DataFrame(rst1)

    # 미래 10년 예측 FCFF 
    ExpFCFF=FCFF.iloc[4,[4]]
    FUTUREFCFF=[]
    for j in range(10):
        ExpFCFF=(df.values[0][j]+1)*ExpFCFF
        FUTUREFCFF.append(ExpFCFF.round(2))
    df.loc[1]=FUTUREFCFF
    #df=df.round(2)
    return df
'''

def FFCFF_CAL(FCFF,FCFF_Grate):
    FCFF_G = FCFF_Grate
    FUTURE=['2019','2020','2021','2022','2023','2024','2025','2026','2027','2028']

    # 미래 10년 예측 FCFF 
    ExpFCFF=FCFF.iloc[4,[4]]
    FUTUREFCFF=[]
    for j in range(10):
        ExpFCFF=(FCFF_G + 1)*ExpFCFF
        FUTUREFCFF.append(ExpFCFF.round(2))

    FUTUREFCFF1=[{FUTURE[i]:FUTUREFCFF[i] for i in range(10)}] #df=df.round(2)
    
    df=pd.DataFrame(FUTUREFCFF1)
    
    return df

def BetaCal(comp):
    
    # 회사 일일 주가지수 crawling
    code_df = pd.read_html('http://kind.krx.co.kr/corpgeneral/corpList.do?method=download&searchType=13', header=0)[0]
    code_df.종목코드 = code_df.종목코드.map('{:06d}'.format) 
    code_df = code_df[['회사명', '종목코드']]
    code_df = code_df.rename(columns={'회사명': 'name', '종목코드': 'code'})

    stockitem = code_df[code_df['name'].str.contains(comp)].values.tolist()[0][1]
    url = 'http://finance.naver.com/item/sise_day.nhn?code='+ stockitem

    html = urlopen(url) 
    source = bs(html.read(), "html.parser")
 
    maxPage=source.find_all("table",align="center")
    mp = maxPage[0].find_all("td",class_="pgRR")
    mpNum = int(mp[0].a.get('href')[-3:])
  
    data = []
    for page in range(1, 125):    #5년치 125
        #print (str(page) )
        url = 'http://finance.naver.com/item/sise_day.nhn?code=' + stockitem +'&page='+ str(page)
        html = urlopen(url)
        source = bs(html.read(), "html.parser")
        srlists=source.find_all("tr")
        isCheckNone = None
    
        #if((page % 1) == 0):
            #time.sleep(1.50)
        for i in range(1,len(srlists)-1):
            if(srlists[i].span != isCheckNone):
                srlists[i].td.text
                price = {"date" : srlists[i].find_all("td",align="center")[0].text, "price" : float((srlists[i].find_all("td",class_="num")[0].text).replace(",","")) }
                data.append(price)
                
    matrixCom = pd.DataFrame(data)

    # kospi200 일일 주가지수 crawling
    url = 'https://finance.naver.com/sise/sise_index_day.nhn?code=KPI200'
    html = urlopen(url) 
    source = bs(html.read(), "html.parser")
 
    maxPage=source.find_all("table",align="center")
    mp = maxPage[0].find_all("td",class_="pgRR")
    mpNum = int(mp[0].a.get('href')[-3:])
 
    data = []
    for page in range(1, 210):      #5년치 210
        url = 'https://finance.naver.com/sise/sise_index_day.nhn?code=KPI200' +'&page='+ str(page)
        html = urlopen(url)
        source = bs(html.read(), "html.parser")
        srlists=source.find_all("tr")
        isCheckNone = None
    
        #if((page % 1) == 0):
            #time.sleep(1.50)

        for i in range(1,len(srlists)-1):
            if(srlists[i].span != isCheckNone):
                srlists[i].td.text
                price = {"date" : srlists[i].find_all("td",class_ = "date")[0].text, "price" : float((srlists[i].find_all("td",class_="number_1")[0].text).replace(",","")) }
                data.append(price)
                
    matrixKospi = pd.DataFrame(data)


    matrixCom["price1"] = np.where(matrixCom['price'] >= 1000000, matrixCom['price']/50, matrixCom["price"])

    matrixSum = pd.merge(matrixCom, matrixKospi,how = 'right', on='date')
    
    cleanedMatrix = matrixSum.iloc[:,[0,2,3]]
  
    cleanedMatrix.columns = ["date","comP","kospi200P"]
    
    cleanedMatrix2 = cleanedMatrix.dropna()
    cleanedMatrix2 = cleanedMatrix2.sort_values(by="date").reset_index(drop=True)

    a = cleanedMatrix2["comP"]
    b = cleanedMatrix2["kospi200P"]
    
    cleanedMatrix2["comR"] = a.pct_change() #2018년 부터 아래로 정렬된 경우 -1 입력
    cleanedMatrix2["kospi200R"] = b.pct_change()

    ssd = cleanedMatrix2["comR"].std(axis=0)
    ksd = cleanedMatrix2["kospi200R"].std(axis=0)
    cor = cleanedMatrix2["comR"].corr(cleanedMatrix2["kospi200R"])
    beta = cor*(ssd/ksd)

    return cleanedMatrix2, beta

def ReturnCal(bMatric, bBeta):
    # 무위험 이자율 계산 
    url = "http://www.index.go.kr/potal/main/EachDtlPageDetail.do?idx_cd=1073"
    html = urlopen(url)
    source = html.read() 
    html.close() 
    soup = bs(source, "html5lib")
    Rf = float(soup.select("tbody > tr > td[data-id='td_107301_14']")[2].string)/100 #10년물 국공채 평균 이자율

    # 시장 수익률 계산
    kospi200=bMatric
    Rm = ((kospi200.iloc[-1][2]-kospi200.iloc[len(kospi200)-820][2])**(1/3))/100
    
    #  주식 수익률 계산
    Re = Rf + (bBeta * (Rm - Rf))

    return Rf, Rm, Re

def EV_Calculation(BS, CF, BW, NO, FCFF, Re, F_FCFF, I_Growth):
    # WACC 계산 
    eRatio = float(BS.iloc[21,5]) / float(BS.iloc[14,5]) # 자본 비율
    dRatio = 1 - eRatio # 부채비율
    iRate = FCFF.iloc[1,4] / BW[4] # 차입금 이자율
    taxR = FCFF.iloc[2].mean() # 법인 세율
    WACC = Re * eRatio + iRate*(1-taxR)*dRatio

    # Terminal Value 및 FCFF 현재가치 계산
    df = F_FCFF
    #df.iloc[1][-1]=df.iloc[1][-1]+df.iloc[1,9]*(1+I_Growth)/(WACC-I_Growth)
    df.iloc[0][-1]=df.iloc[0][-1]+df.iloc[0,9]*(1+I_Growth)/(WACC-I_Growth)

    p = pd.DataFrame([np.zeros(10)],columns=['2019','2020','2021','2022','2023','2024','2025','2026','2027','2028'])
    df = df.append(p, ignore_index=True)
    #print(df)
    DiscountedFCFF=[]
    for i in range(10):
        #DiscountedFCFF.append(df.iloc[1][i]/((1+WACC)**(i+1)))
        DiscountedFCFF.append(df.iloc[0][i]/((1+WACC)**(i+1)))
    DiscountedFCFF

    #df.iloc[2]=DiscountedFCFF
    df.iloc[1]=DiscountedFCFF
    #EnterpriseValue=df.iloc[2].sum()
    EnterpriseValue=df.iloc[1].sum()
    EquityValue=EnterpriseValue-BW[-1]
    StockPrice = EquityValue / NO
    return iRate, WACC, EnterpriseValue, EquityValue, StockPrice

def OpinionDecision(bMatric, StockPrice):
    todayP = bMatric.iloc[-1,1]
    StockPrice = float(StockPrice)

    #print("Today's Stock Price", todayP)
    if StockPrice >=  (todayP * 1.2):
        opinion = "BUY"
    elif StockPrice >= (todayP * 0.8):
        opinion = "HOLD" 
    else:
        opinion = "SELL"
    print(opinion)

    return todayP, opinion
    
def PRINT(comp, Rf, Rm, Re, iRate, WACC, EnterpriseValue, EquityValue, StockPrice, todayP, opinion):
    EnterpriseValue = float(EnterpriseValue)
    EquityValue = float(EquityValue)
    StockPrice = float(StockPrice)
    print("***{} 주가에 대한 정보입니다.***".format(comp))
    print("==============================================================")
    print("")
    print("10년물 국공채 이자율 : {0:.2f}".format(Rf))
    print("KOSPI200 연 수익률 : {0:.2f}".format(Rm))
    print("{0}주식의 예상 수익률 : {1:.2f}".format(comp, Re))
    print("{0}의 차입이자율 : {1:.2f}".format(comp, iRate))
    print("{0}의 가중평균 투자 수익률 : {1:.2f}".format(comp, WACC))
    print("{0}의 예상 기업가치 : {1:.2f}".format(comp, EnterpriseValue))
    print("{0} 주식의 예상 시장가치 : {1:.2f}".format(comp, EquityValue))
    print("==============================================================")
    print("")
    print("{0} 주식의 예상 주당가치 : {1:.2f}".format(comp, StockPrice))
    print("{0} 주식의 현재 거래가격 : {1:.2f}".format(comp, todayP))
    print("==============================================================")
    print("")
    print("{0} 주식에 대한 의견 : {1}".format(comp, opinion))
    return None

def InsertMongDB(comp, Rf, Rm, Re, iRate, WACC, EnterpriseValue, EquityValue, StockPrice, todayP, opinion):
    EnterpriseValue = float(EnterpriseValue)
    EquityValue = float(EquityValue)
    StockPrice = float(StockPrice)

    # 외부서버의 docker에 설치된 mongoDB 접속
    myclient = pymongo.MongoClient('mongodb://192.168.103.103:27017/') #192.168.103.103
   
    #database 생성
    mydb = myclient['EnterpriseVlaueTest']  #libraryBooks

    #collection 생성
    mycol = mydb["EV"]
    
    #data 입력(insert)
    mydict = {  "comp" : comp,
                "Rf" : Rf,
                "Rm" : Rm,
                "Re" : Re,
                "iRate" : iRate,
                "WACC" : WACC,
                "EnterpriseValue" : EnterpriseValue,
                "EquityValue" : EquityValue,
                "StockPrice" : StockPrice,
                "todayP" : todayP,
                "opinion" : opinion }
    
    mycol.insert_one(mydict)

    print("")
    print("==============================================================")
    print("MongoDB 출력")
    print("")
    #data 찾기(find)
    for x in mycol.find():
        print(x)
    print("==============================================================")

if __name__ == "__main__":
    main()