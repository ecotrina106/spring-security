package com.app.controller.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public record AuthCreateRoleRequest(@Size(max = 3, message = "El usuario no puede tener más de 3 roles")
                                    List<String> roleListName) {
}
