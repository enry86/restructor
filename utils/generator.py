#!/usr/bin/env python
'''
Utlity for dataset generation
'''

import sys
import random

names = \
['linda','enrico','luca','john','jack','mattew','anakin','leia','gianni','paolo','mario','jerry','alan']
surnames = \
['rossi','bianchi','verdi','cavallo','skywalker','oro','berlusconi','marx','lucas','sartori','jackson']
cities = \
['trento','molveno','newyork','chicago','roma','milano','sfruz','parma','pavia','bologna','paris','pechino']

num = int(sys.argv[1])
r = random.Random()

file = open('gen_dataset.xml','w')
file.write('<entities>\n')

for i in range(num):
    file.write('<entity id="'+str(i)+'">\n')
    struc = r.randint(0,2)
    n = r.randint(0,len(names)-1)
    s = r.randint(0,len(surnames)-1)
    c = r.randint(0,len(cities)-1)
    if struc == 0:
        file.write('<name>' + names[n] + '</name>\n')        
        file.write('<surname>' + surnames[s] + '</surname>\n')        
        file.write('<city>' + cities[c] + '</city>\n')
    elif struc == 1:
        file.write('<person>\n')
        file.write('<name>'+names[n]+'</name>\n')        
        file.write('<surname>'+surnames[s]+'</surname>\n')        
        file.write('<city>'+cities[c]+'</city>\n')
        file.write('</person>\n')
    elif struc == 2:
        file.write('<person>' + names[n] + ' ' + surnames[s] + ' ' + \
        cities[c] + '</person>\n')
    file.write('</entity>\n')

file.write('</entities>')
file.close()
