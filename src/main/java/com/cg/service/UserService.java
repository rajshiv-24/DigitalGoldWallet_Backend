package com.cg.service;

import com.cg.dto.UserRequestDTO;
import com.cg.dto.UserResponseDTO;
import com.cg.dto.WalletTopUpRequestDTO;
import com.cg.dto.PaymentResponseDTO;

import java.util.List;

public interface UserService {


    UserResponseDTO createUser(UserRequestDTO request);

    UserResponseDTO getUserById(Integer userId);

   
    List<UserResponseDTO> getAllUsers();


    UserResponseDTO updateUser(Integer userId, UserRequestDTO request);

  
    void deleteUser(Integer userId);

   
    PaymentResponseDTO topUpWallet(WalletTopUpRequestDTO request);
}
