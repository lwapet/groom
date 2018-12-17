import os
import time
import requests
import random
import hashlib
from pymongo import MongoClient
from multiprocessing import Pool, cpu_count
from subprocess import run, PIPE

_QUANTITY = 5
_TOTAL_COUNT = 0
_PATH_TO_JAR = './build/libs/static-0.2.0.jar'
_JAR_ANALYZER_CONFIG_FILE_PATH = './main.json'
_PATH_TO_APK_DIRECTORY = '/Users/lgitzing/Development/work/ransomwares'
_DATABASE_URL = 'localhost'
_DATABASE_PORT = 27017
_DATABASE_NAME = 'dynamic'
_DYNAMIC_COLLECTION_NAME = 'dynamic'
_APPLICATION_COLLECTION_NAME = 'application'
# _URI = 'mongodb://lgitzing:tout_petit_poney@localhost:27017/analyzer?authSource=admin'
_URI = 'mongodb://localhost:27017/dynamic'


def select_apks(quantity, database):  # analyses_count = {'analyses': {'$size': 0}} # is_hybrid = {'is_hybrid': True}
    vt_count = {'vt_detection': {'$gt': 2}}
    id = {
        '_id': '000026E846FFCF60E2A9E135251B4F411300E903DCE95D47989B24158E93BB3D'}
    criterias = [id]
    apps = database[_APPLICATION_COLLECTION_NAME].find({'$and': criterias}).limit(
        quantity)
    return apps


def pipeline(file_path):
    print("pipeline started for hash : " + file_path)
    command = ['java',
               '-Xmx6g',
               '-jar', _PATH_TO_JAR,
               '-c', _JAR_ANALYZER_CONFIG_FILE_PATH,
               '-a', _PATH_TO_APK_DIRECTORY + '/' + file_path]
    process = run(args=command, stdout=PIPE, stderr=PIPE, timeout=600)

    if process.returncode != 0:
        print('stderr : \n' + process.stderr.decode('utf-8'))
    else:
        print('stdout : \n' + process.stdout.decode('utf-8'))
        print('stderr : \n' + process.stderr.decode('utf-8'))


def callback(r):
    global _TOTAL_COUNT
    _TOTAL_COUNT = _TOTAL_COUNT + 1
    print('processed ' + str(_TOTAL_COUNT) + '/' + str(_QUANTITY))


def start_pool(apks):
    pool = Pool(cpu_count())
    for apk_data in apks:
        x = pool.apply_async(pipeline, args=(apk_data,), callback=callback)
    pool.close()
    pool.join()


def main():
    client = MongoClient(_URI)
    database = client[_DATABASE_NAME]
    analyzed_apps_sha = database[_APPLICATION_COLLECTION_NAME].distinct("sha256")
    files = os.listdir(_PATH_TO_APK_DIRECTORY)
    chosen_files = []
    while len(chosen_files) < _QUANTITY:
        file_path = random.choice(os.listdir(_PATH_TO_APK_DIRECTORY))
        sha256 = hashlib.sha256()
        with open(_PATH_TO_APK_DIRECTORY + '/' + file_path, 'rb') as f:
            for byte_block in iter(lambda: f.read(4096), b""):
                sha256.update(byte_block)
        digest = sha256.hexdigest()
        # print(analyzed_apps_sha)
        if digest.upper() not in analyzed_apps_sha:
            chosen_files.append(file_path)
    files = files[:_QUANTITY]
    start_pool(chosen_files)


if __name__ == '__main__':
    main()
