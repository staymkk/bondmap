package com.example.bondmap_backend.exception

class BondNotFoundException(id: Long) :
    RuntimeException("Bond with id $id not found")