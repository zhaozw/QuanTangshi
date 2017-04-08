import glob
import os
import json
import re
import sqlite3
from zipfile import ZipFile
from collections import Counter

empty_count = 0


class Poem:
    __slots__ = ('id', 'title', 'author', 'text')

    def __init__(self, id, title, author, text):
        self.id = id
        self.title = title
        self.author = author

        text = re.sub(r'(？|！)', '\1\n', text)
        text = re.sub(r'^YY', '', text, flags=re.M)
        assert 'YY' not in text, '出现YY字符'

        self.text = text

    def get_tuple(self):
        return (self.id,
                self.title.encode('utf_16_le'),
                self.author.encode('utf_16_le'),
                self.text.encode('utf_16_le'))

    def __str__(self):
        return 'ID:%d\n标题：%s\n作者：%s\n内容：\n%s\n' % \
                (self.id, self.title, self.author, self.text)


def process_paragraphs(paragraphs):
    for i in range(0, len(paragraphs)):
        # 去掉 -2222-
        m = re.search(r'-\d+-?', paragraphs[i])
        if m:
            p = re.sub(r'-\d+-?', r'', paragraphs[i])
            paragraphs[i] = p

        # 只含。的行
        if paragraphs[i] == '。':
            paragraphs[i] = ''

    return '\n'.join(p for p in paragraphs if p != '')


def load_poem():
    def key(s):
        r = re.search(r'\.(\d+)\.', s)
        r = r.group(1)
        return int(r)

    l = glob.glob('json/poet.tang.*.json')
    l.sort(key=key)

    lst = []
    id = 1
    for fn in l:
        #print('正在加载文件', fn)

        with open(fn, encoding='utf-8') as f:
            obj = json.load(f)

        for d in obj:
            title = d['title']
            author = d['author']

            paras = d['paragraphs']
            text = process_paragraphs(paras)

            if not text:
                global empty_count
                empty_count += 1

            p = Poem(id, title, author, text)

            m = re.search(r'[^\u0000-\uffff]', str(p))
            if m:
                raise Exception('出现non-BMP字符！')

            lst.append(p)
            id += 1

    print('载入%d条记录' % len(lst))
    return lst


def create_db():
    db = sqlite3.connect('tangshi.db', isolation_level=None)

    # 建表
    sql = ('CREATE TABLE poem('
           'id INTEGER PRIMARY KEY,'
           'title BLOB,'
           'author BLOB,'
           'txt BLOB);')
    db.execute(sql)

    return db


def save_close_db(lst, db):
    sql = 'INSERT INTO poem VALUES(?,?,?,?);'
    db.execute('BEGIN')
    for p in lst:
        db.execute(sql, p.get_tuple())
    db.commit()

    # 关闭数据库
    db.execute('VACUUM')
    db.close()


def main():
    # 删已有
    try:
        os.remove('tangshi.db')
    except Exception as e:
        print(e)

    try:
        os.remove('tangshi.db.zip')
    except Exception as e:
        print(e)

    # 诗
    lst = load_poem()

    # 建数据库
    db = create_db()
    # 写数据库
    save_close_db(lst, db)

    # zip
    with ZipFile('tangshi.db.zip', 'w') as myzip:
        myzip.write('tangshi.db')

    print('无内容的记录%d条' % empty_count)
    print('运行完毕')

main()
