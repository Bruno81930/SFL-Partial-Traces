import pydriller
import subprocess
import re
from tqdm import tqdm
import sys

def extract_ground_truths(project, version):
    output = subprocess.run(['defects4j', 'info', '-p', project, '-b', version], stdout=subprocess.PIPE, stderr=subprocess.PIPE).stdout.decode('utf-8')

    program = re.search(r'^\s*Program:\s*(.+)$', output, re.MULTILINE).group(1)
    path = f"repos/{project}"
    commit = re.search(r'^Revision ID \(fixed version\):\s*\n([a-f0-9]+)$', output, re.MULTILINE).group(1)

    common_prefixes = ["src/main/java", "src/test/java", "src/java", "src/test", "src/main"]

    repo = pydriller.Repository(path, only_commits=[commit])
    commit = next(repo.traverse_commits())
    modified_files = commit.modified_files

    patch_path = f"ground_truth/{project}/{version}/patch.txt"
    patch = open(patch_path, "r").read()

    methods = list()
    for mfile in modified_files:
        if (mfile.old_path is None):
            continue
        
        package_path = mfile.old_path
        for prefix in common_prefixes:
            if package_path.startswith(prefix):
                package_path = package_path[len(prefix):]
                break
            elif package_path.startswith(project + "/" + prefix):
                package_path = package_path[len(project + "/" + prefix):]
                break
        if package_path.startswith("/"):
            package_path = package_path[1:]
        package_signature = package_path.rsplit("/",1)[0].replace('/', '.')
        print(package_signature)
        for method in mfile.changed_methods:
            if method.name.split("::")[0] in patch:
                methods.append(package_signature + "." + method.name)
    return methods

project = sys.argv[1]
version = sys.argv[2]
print(project + "_" + version)
methods = extract_ground_truths(project, version)
if (len(methods) == 0):
	with(open(f"failed.txt", "w")) as file:
        	file.write(f"{project},{version}")
else:
	with(open(f"ground_truth/{project}/{version}/ground_truth.txt", "w")) as file:
		file.write("\n".join(methods))
