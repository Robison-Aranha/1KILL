
import os

NUM = 37
ARRAY = []
FINAL_AMOUNT = {}
SQUARES = []
MATCHNUMBERS = {"number -> square": {}, "number -> size":{}, "size -> number":{}}


def generateArray():
    for c in range(1, NUM + 1):
        ARRAY.append(c)


def findSquares():
    count = 2
    while True:
        square = count ** 2
        if square > (NUM + (NUM + 1)): 
            break
        SQUARES.append(square)
        count += 1
        

# def sortSquares():
    
#     SORTEDSQUARES.clear()
    
#     for c in MATCHSQUARES.keys():
    
#         for key, value in enumerate(SORTEDSQUARES):
            
#             if MATCHSQUARES[c] > MATCHSQUARES[value]:
                
#                 SORTEDSQUARES.insert(key, c)
#                 break
        
        
#         if c not in SORTEDSQUARES:
        
#             SORTEDSQUARES.append(c)
            
        
            
def matchNumbers():

    for c in ARRAY:
        list = []
        add = False
        for i in SQUARES:
            # if MATCHSQUARES.get(i) == None:
            #     MATCHSQUARES[i] = 0
            if i > c:
                number = i - c
                if number <= NUM and number != c and number in ARRAY:
                    
                    if FINAL_AMOUNT["direita"].get(c) and FINAL_AMOUNT["direita"][c][0] != number:
                        add = True
                    elif FINAL_AMOUNT["esquerda"].get(c) and FINAL_AMOUNT["esquerda"][c][-1] != number:
                        add = True
                    elif FINAL_AMOUNT["direita"].get(c) == None and FINAL_AMOUNT["esquerda"].get(c) == None:
                        add = True
                        
                    if add:
                        list.append(number)
                    # MATCHSQUARES[i] += 1
        if len(list) > 0:
            MATCHNUMBERS["number -> square"][c] = list
        
        
def matchSizeNumbers():
    
    for c in MATCHNUMBERS["number -> square"].keys():
        sizeList = len(MATCHNUMBERS["number -> square"][c])
        if MATCHNUMBERS["size -> number"].get(sizeList) == None:
            MATCHNUMBERS["size -> number"][sizeList] = []
        if MATCHNUMBERS["number -> size"].get(c) == None:
            MATCHNUMBERS["number -> size"][c] = sizeList
            
        MATCHNUMBERS["size -> number"][sizeList].append(c)
    

def removeNumbers(num, proxNum):
    
    list = [num, proxNum]
    
    if FINAL_AMOUNT["direita"].get(num) == None and FINAL_AMOUNT["esquerda"].get(num) == None:
        
        for c in list:
        
            if MATCHNUMBERS["number -> square"].get(c):
                for i in MATCHNUMBERS["number -> square"][c]:
                    if MATCHNUMBERS["number -> square"].get(i):
                        if num in MATCHNUMBERS["number -> square"][i]:
                            size = MATCHNUMBERS["number -> size"][i]
                            MATCHNUMBERS["size -> number"][size].remove(i)
                            size -=1
                            MATCHNUMBERS["size -> number"][size].append(i)
                            MATCHNUMBERS["number -> size"][i] = size
                            MATCHNUMBERS[i].remove(c)
            
            
            MATCHNUMBERS["size -> number"][MATCHNUMBERS["number -> size"][c]].remove(c)
            del MATCHNUMBERS["number -> size"][c]
    else:   
    
        for c in list:
        
            size = MATCHNUMBERS["number -> size"][c]
            MATCHNUMBERS["size -> number"][size].remove(c)
            size -= 1
            MATCHNUMBERS["size -> number"][size].append(c)
            MATCHNUMBERS["number -> size"][c] = size
            MATCHNUMBERS[c].remove(num if c == proxNum else proxNum)

        
def organizeArray():
    minSize = min(MATCHNUMBERS["size -> number"].keys())
    num = MATCHNUMBERS["size -> number"][minSize][-1]
    while True:
        
        senseProxNum = ""
        senseNum = ""
        
        list = []
        proxNum = ""
        
        for c in MATCHNUMBERS["number -> square"][num]:
            list.append(MATCHNUMBERS["number -> size"][c])
        
        minSize = min(list)
        
        for c in MATCHNUMBERS["number -> square"][num]:
            if MATCHNUMBERS["number -> size"][c] == minSize:
                proxNum = c
                break
        
        sequenceProxNum = []
        sequenceNum = []
        newSequence = []
        inFinalAmoutNum = True
        inFinalAmoutProxNum = True
        
        if FINAL_AMOUNT["direita"].get(num):
            senseNum = -1
            sequenceNum = FINAL_AMOUNT["direita"][num]
        elif FINAL_AMOUNT["esquerda"].get(num):
            senseNum = 0
            sequenceNum = FINAL_AMOUNT["esquerda"][num]
        else:
            inFinalAmoutNum = False
            sequenceNum = [num]
        
        if FINAL_AMOUNT["direita"].get(proxNum):
            senseProxNum = -1
            sequenceProxNum = FINAL_AMOUNT["direita"][proxNum]
        elif FINAL_AMOUNT["esquerda"].get(proxNum):
            senseProxNum = 0
            sequenceProxNum = FINAL_AMOUNT["esquerda"][proxNum]
        else:
            inFinalAmoutProxNum = False
            sequenceProxNum = [proxNum]
            
        
        if senseNum == senseProxNum:
            sequenceProxNum.reverse()
            
        newSequence = sequenceNum + senseProxNum if senseNum == -1 else senseProxNum + sequenceNum
        
        if inFinalAmoutNum:
            FINAL_AMOUNT["direita"].pop(num)
            FINAL_AMOUNT["esquerda"].pop(sequenceNum[0])
            
        if inFinalAmoutProxNum:
            FINAL_AMOUNT["esquerda"].pop(proxNum)
            FINAL_AMOUNT["direita"].pop(sequenceProxNum[-1])
    
            
        FINAL_AMOUNT["direita"][newSequence[-1]] = newSequence
        FINAL_AMOUNT["esquerda"][newSequence[0]] = newSequence
        
        
            
        if len(newSequence) == NUM:
            break
        
        removeNumbers(num)
        
        
        
    
    
            
        
generateArray()
findSquares()
matchNumbers()
matchSizeNumbers()




os.system('cls') 

print(MATCHNUMBERS["number -> square"])
print(100 * "-=")
print(MATCHNUMBERS["size -> number"])
print(100 * "-=")
print(MATCHNUMBERS["number -> size"])
print(100 * "-=")
print(SQUARES)






