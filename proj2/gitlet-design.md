# Gitlet Design Document

**Edward Tsang**:

## Classes and Data Structures

### Class Repository

#### Fields

1. Directories and file
   * CWD
   * GITLET_DIR
   * ...
   * HEAD
2. static TreeMap blobs


### Class Commit

#### Fields

1. String uid
2. String log
3. String parent
4. Date date
5. TreeMap blobs

### Class Blob

#### Fields

1. File file
2. byte[] content

## Algorithms
* TreeMap
* Hashing

## Persistence
* commits
* HEAD
* blobs

