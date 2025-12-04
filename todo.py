#!/usr/bin/python3

import os
import argparse

def find_files_without_marker(start_dir, marker, extension_filter, ignore_dirs):
    """
    Recursively walks through a directory and prints files that
    do not contain the 'marker' string in the first line.

    Args:
        start_dir (str): The path to the directory to start searching from.
        marker (str): The string to search for in the first line.
        extension_filter (str | None): File extension to filter by (e.g., ".py").
        ignore_dirs (list[str]): A list of directory names to ignore.
    """

    file_count = 0

    # Standardize the extension filter to start with a dot if it exists
    if extension_filter and not extension_filter.startswith('.'):
        extension_filter = '.' + extension_filter

    # os.walk recursively visits all directories and files
    # (root) is the current directory path
    # (dirs) is a list of subdirectories in root
    # (files) is a list of files in root
    for root, dirs, files in os.walk(start_dir, topdown=True):
        
        # Prune ignored directories:
        # By modifying 'dirs' in-place, we tell os.walk not to descend
        # into these directories.
        dirs[:] = [d for d in dirs if d not in ignore_dirs]

        for file in files:
            # --- Handle Extension Filter ---
            # If a filter is provided, skip files that don't match
            if extension_filter and not file.endswith(extension_filter):
                continue
            
            file_path = os.path.join(root, file)

            try:
                # Open the file and read only the first line
                # We use encoding='utf-8' and errors='ignore'
                # to safely handle potential binary files or
                # files with different encodings without crashing.
                with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                    first_line = f.readline()

                    # This is the core check:
                    # Is the marker string *not* found in the first line?
                    if marker not in first_line:
                        print(f"File missing marker: {file_path}")
                        file_count += 1

            except Exception as e:
                # Handle other potential errors, like permissions issues
                print(f"Could not read file {file_path}: {e}")

    return file_count

def parse_args():
    parser = argparse.ArgumentParser(
        description='Find files that DO NOT contain a marker string in the first line.'
    )
    parser.add_argument(
        '-d', '--directory', 
        type=str, 
        required=False, 
        default='.', 
        help='Directory to start the search from. Default is current directory.'
    )
    parser.add_argument(
        '-i', '--ignore-dir', 
        action='append', 
        default=['.idea', 'target', '.vscode', '.mvn'], 
        help='Directory NAME to ignore. Can be used multiple times.'
    )
    parser.add_argument(
        '-e', '--extension', 
        type=str, 
        required=False, 
        default='.java', 
        help='Only show files with the specified extension. Default is .java.'
    )
    parser.add_argument(
        '-m', '--marker', 
        type=str, 
        required=False, 
        default='// DONE', 
        help='Specify a marker to search. Default is "// DONE"'
    )
    return parser.parse_args()

def main():
    args = parse_args()

    # Check if the marker is an empty string
    if args.marker == '':
        print("--- WARNING ---")
        print("Your marker string is empty.")
        print("An empty string is 'contained' in every file's first line,")
        print("so this script will likely find no files.")
        print("---------------")
        return

    start_path = os.path.abspath(args.directory)
    print(f"Searching for files in '{start_path}'...")
    if args.ignore_dir:
        print(f"Ignoring directory names: {', '.join(args.ignore_dir)}")
    if args.extension:
        print(f"Filtering for extension: {args.extension}")
        
    print(f"Reporting files where the first line does NOT contain:")
    print(f"'{args.marker}'\n")

    file_count = find_files_without_marker(
        start_dir=args.directory, 
        marker=args.marker,
        extension_filter=args.extension,
        ignore_dirs=args.ignore_dir
    )

    print(f"\nSearch complete. {file_count} files listed.")

if __name__ == "__main__":
    main()
