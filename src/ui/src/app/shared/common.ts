export function fileListToArray(fileList: FileList): File[] {
    const files = [];
    for (let i = 0, l = fileList.length; i < l; i++) {
        files.push(fileList[i]);
    }
    return files;
}
