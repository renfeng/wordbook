# What it does

This app keeps a list of words as you learn them. The list is stored on your device.

You start with an empty list. The list will grow each time you enter a new word, and will be removed when you uninstall the app, or remove the Word Book from Settings > Accounts.

The definition of words are retrieved from http://www.oxfordlearnersdictionaries.com/

You can consider it a special browser for OALD with a lazy, and permanent cache.

# How to use

Start the app, enter a word, and click the Lookup button. Word definition will be downloaded at appropriate moment (with a max delay of 30 seconds) to conserve your battery.

Why not download immediately (synchronously)? This is my Final Project for https://www.udacity.com/course/ud853. It demonstrates the usage of a sync adapter. Another reason is [a feature in planning](https://github.com/renfeng/wordbook/issues/1).

When the word definition is ready to read, you'll get a notification.

You can also read again a word looked up previously by choosing it from the word list.

# Q&A

## When to expect the definition to appear on the screen

Word definition view won't get updated until you navigate away and back it. Same problem with the word list.

The problem is not so obvious on phones, but it makes life difficult on tablets, on which app runs in two-pane layout. You'll have to rely on task manager or notification to get the list refreshed. (Sorry for that! If you know how to [implement the feature of updating UI on a background event](https://github.com/renfeng/wordbook/issues/2), please show me. I'll be a good learner :)

## What the number next to the word signifies

It's the view count. In other words, it shows how many times you've read the word definition (before this time you see the number).

## How I can tell if I typed in a valid word or not

I would crash in an earlier version. Now, you'll read an appropriate message.

# Devices Word Book tested on

## Handware devices

Nexus 5, Lollipop

## AVDs

https://plus.google.com/photos/+FrankR/albums/6104180924507847601?authkey=CNn16rS9z7Xzcw

# Troubleshoot

If you don't get a notification for a new word, kill the app, and start it again.

If that won't help, please create an issue [here](https://github.com/renfeng/wordbook/issues).
