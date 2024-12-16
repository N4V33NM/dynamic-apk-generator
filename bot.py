import os
from flask import Flask, request
import requests

app = Flask(__name__)

REPO_OWNER = "N4V33NM"  # Replace with your GitHub username
REPO_NAME = "dynamic-apk-generator"       # Replace with your public repository name
BOT_TOKEN = "8178078713:AAGOSCn4KEuvXC64xXhDrZjwQZmIy33gfaI"  # Replace with your Telegram bot token

@app.route(f"/{BOT_TOKEN}", methods=["POST"])
def handle_message():
    data = request.json
    chat_id = data['message']['chat']['id']
    command = data['message']['text']

    if command == "/request_apk":
        response = trigger_github_action(chat_id)
        if response.status_code == 204:
            send_message(chat_id, "Your APK is being generated. Please wait.")
        else:
            send_message(chat_id, "Error in generating your APK. Try again later.")
    return "OK"

def trigger_github_action(chat_id):
    # API endpoint for triggering GitHub Actions workflow dispatch event
    url = f"https://api.github.com/repos/{REPO_OWNER}/{REPO_NAME}/actions/workflows/build-apk.yml/dispatches"

    # GitHub API payload
    payload = {
        "ref": "main",  # Branch name
        "inputs": {"chat_id": str(chat_id)}  # Passing chat ID as input
    }

    # No authentication required for public repositories
    return requests.post(url, json=payload)

def send_message(chat_id, text):
    # Sending a message to Telegram bot
    url = f"https://api.telegram.org/bot{BOT_TOKEN}/sendMessage"
    requests.post(url, data={"chat_id": chat_id, "text": text})

if __name__ == "__main__":
    app.run(port=5000)
