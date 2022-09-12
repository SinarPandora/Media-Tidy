export interface TidyHistory {
    id: number
    fileId: number
    originTagId: number | null
    originSourceId: number | null
    originFolder: string | null
    originFilename: string | null
    tidiedTagId: number | null
    tidiedFolder: string | null
    tidiedFilename: string | null
    createAt: Date
    action: string
    revert: boolean
}
