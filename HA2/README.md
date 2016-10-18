# FTP Client-Server

## Building

```
 user@user$ gradle installDist
```
After that, `ftp-client` executable will be generated in `./build/install/ftp-client/bin`.

## Usage

- `ftp-client server` starts server. It will look for a `storage` folder in current directory, that will be later used as a source of files on server. If there are no such folder, error will be reported.

- `ftp-client list` will output all **files** in `storage` directory. No directories, no recursive walk - only files.

- `ftp-client get FILE_NAME` will try to download file with provided name from server. File will be placed in the current directory with name `FILE_NAME.download`. If such a file already exists, error will be reported.
