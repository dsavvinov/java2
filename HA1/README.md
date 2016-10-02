# Gut - next-generation (no) VCS

## Building

```
    $ gradle installDist
```

After that, you can find gut-executable in `/build/install/gut/bin`

## Usage

For most of commands, Gut copies behaviour of Git. However, there are
a few important notes:

1. Gut doesn't clean garbage in the storage, i.e. if some file was ever
staged, this version will be keeped forever. In later releases this may be
changed.

2. `gut init` **will completely remove** any existing .gut-folder. Be careful!
 
3. Gut drastically differs from Git in terms of merging. 
   1. Anything except additions of new files is treated as merge conflict.

   2. In case of merge conflict, *Gut* will place all conflicted files in 
  working tree in two versions: `file-name.our` for the revision from the current
  branch and `file-name.their` for the revision from the other branch.
  
   3. Merge conflicts should be resolved manually. After that, you can **commit**
  changes. Note that calling `merge` won't always lead to automerge even if conflicts 
  just have been resolved! 
  
      E.g., if current branch contains some file `foo`, and other branch doesn't, and you want to keep
   `foo` after merge - then `merge` will always find conflict (Gut doesn't know if
   it should remove or keep `foo`, and currently there are no way to tell him)
   
  4. Sometimes, Gut can autoresolve conflicts. Sometimes.