export interface Setting {
    id: number
    key: string
    value: string
    valueType: string
    visible: boolean
}

export const Settings = {
    CURRENT_PROFILE: "CURRENT_PROFILE"
}
