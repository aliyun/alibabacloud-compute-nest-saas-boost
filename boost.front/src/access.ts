/**
 * @see https://umijs.org/zh-CN/plugins/plugin-access
 * */
export default function access(initialState: { currentUser?: API.UserInfoModel } | undefined) {
  const { currentUser } = initialState ?? {};
  return {
    admin: currentUser && currentUser.admin
  };
}
