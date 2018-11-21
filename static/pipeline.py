import os
import time
import requests
from multiprocessing import Pool, cpu_count
from subprocess import run, PIPE



_QUANTITY = 5
_TOTAL_COUNT = 0
_PATH_TO_JAR = './build/libs/static-0.2.0.jar'
_JAR_ANALYZER_CONFIG_FILE_PATH = './main.json'
_PATH_TO_APK_DIRECTORY = '/Users/lgitzing/Development/work/ransomwares'

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
    files = os.listdir(_PATH_TO_APK_DIRECTORY)
    files = files[:_QUANTITY]
    start_pool(files)


if __name__ == '__main__':
    main()
