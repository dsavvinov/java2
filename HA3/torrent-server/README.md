# Torrent

## Building

```bash
$ gradle installDist
$ cd build/install/torrent/bin
```

Here you can find `server`, `client` and `ui`-executables. See how to launch them
below.


## Launching

**Note**. You have to install MongoDB with version ⩾ 3.2. MongoDB should run on the
default port (27017).

`client` and `ui` are one and the same clients with diffrent interfaces (command
line and GUI). You have to pass port of client as the first argument, and you may
pass non-default DB name for MongoDB as the second argument. By default, client
will use "torrent-client-db" database.

```
  $ ./client <CLIENT PORT> [DB NAME]
```

`server` always runs on port, specified in utils/Constants.java (8081 by default).
Also, `server` always uses "torrent-server-db" database in MongoDB.

> Here and below we will refer to the directory, from which you have launched `client` or `ui`, as *root directory*.

## Using CLI

CLI is implemented via REPL.  You can use following commands here:

  - `exit`. Shutdown and leave REPL gracefully.
  - `list`. Display all files on the tracker.
  - `sources <FILE ID>`. Display all sources of the file with specified ID.
  - `upload <RELATIVE PATH TO FILE>`. Uploads file on the tracker. Note, that
    according to the protocol, **you have to send an `update`** next, otherwise **you won't
    appear as seed** to the other peers.
  - `update`. Sends an update to the tracker.
  - `stat <PEER ADDRESS> <PEER PORT> <FILE ID>`. Display parts of file, owned by the specified peer.
  - `get <PEER ADDRESS> <PEER PORT> <FILE ID> <PART ID>`. Downloads specified part of the file from peer. File will be created in the *root directory*.
  - `download <FILE ID>`. Downloads file with the specified id in automatic mode.

## Using GUI

Use `Upload` dialog to upload files to tracker.

Use `List` to get list of files.

Select file in table and click `Download` to download it in the *root directory*.
