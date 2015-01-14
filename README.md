# What it does

This app keeps a list of words as you learn them. The list is stored on your device.

You start with an empty list. The list will grow each time you enter a new word, and will be removed when you uninstall the app.

The definition of words are retrieved from http://www.oxfordlearnersdictionaries.com/

You can consider it a special browser for OALD with a lazy, and permanent cache.

# How to use

Start the app, enter a word, and click the Lookup button. Word definition will be downloaded at appropriate moment (with a max delay of 30 seconds) to conserve your battery.

When the word definition is ready to read, you'll get a notification.

You can also read again a word looked up previously by choosing it from the word list.

# Q&A

## When to expect the definition to appear on the screen

Word definition view won't get updated until you close and open it again. Same problem with the word list. If you know how to implement the feature of updating UI on a background event, please show me. I'm a good learner :)

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
