package cn.flyingocean.fileship.dto;

import cn.flyingocean.fileship.domain.User;

public class UserDTO {

    private int id;
    private String email;
    private String nickname;
    private String phoneNumber;
    private int rootWarehouseId;

    /**
     * 根据 User 填充自己
     * @param user
     * @return
     */
    public static UserDTO buildFromUser(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setId(user.getId());;
        userDTO.setNickname(user.getNickname());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setRootWarehouseId(user.getRootWarehouseId());
        return userDTO;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setRootWarehouseId(int rootWarehouseId) {
        this.rootWarehouseId = rootWarehouseId;
    }

    public int getRootWarehouseId() {
        return rootWarehouseId;
    }
}
